package com.d4rk.cartcalculator.ui.screens.cart

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.data.model.ui.screens.UiCartScreen
import com.d4rk.cartcalculator.ui.screens.cart.repository.CartRepository
import com.d4rk.cartcalculator.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(application : Application) : BaseViewModel(application) {

    private val repository : CartRepository = CartRepository()

    private val _uiState : MutableStateFlow<UiCartScreen> = MutableStateFlow(UiCartScreen())
    val uiState : StateFlow<UiCartScreen> = _uiState

    fun loadCart(cartId : Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            showLoading()
            val cart : ShoppingCartTable? = repository.loadCartIdImplementation(cartId = cartId)
            cart?.let { safeCart ->
                repository.loadCartItemsRepository(cartId = cartId) { items ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            cartItems = items , cart = safeCart
                        )
                    }
                    calculateTotalPrice()
                }
            } ?: kotlin.run {}
            hideLoading()
            initializeVisibilityStates()
        }
    }

    private fun initializeVisibilityStates() {
        viewModelScope.launch(coroutineExceptionHandler) {
            delay(timeMillis = 50L)
            _visibilityStates.value = List(size = _uiState.value.cartItems.size) { false }
            _uiState.value.cartItems.indices.forEach { index ->
                delay(timeMillis = index * 8L)
                _visibilityStates.value = List(size = _visibilityStates.value.size) { lessonIndex ->
                    lessonIndex == index || _visibilityStates.value[lessonIndex]
                }
            }
        }
    }

    fun loadSelectedCurrency(dataStore : DataStore) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(
                    selectedCurrency = dataStore.getCurrency().firstOrNull() ?: ""
                )
            }
        }
    }

    fun addCartItem(cartId : Int , cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            cartItem.cartId = cartId
            repository.addCartItemRepository(cartItem = cartItem) { newItem ->
                _uiState.update { currentState ->
                    val cartItems : List<ShoppingCartItemsTable> = currentState.cartItems + newItem
                    currentState.copy(cartItems = cartItems)
                }
                calculateTotalPrice()
            }
        }
        initializeVisibilityStates()
    }

    fun increaseQuantity(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val currentQuantity : Int = getQuantityStateForItem(cartItem = cartItem)
            updateItemQuantity(cartItem = cartItem , newQuantity = currentQuantity + 1)
        }
    }

    fun decreaseQuantity(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val currentQuantity : Int = getQuantityStateForItem(cartItem = cartItem)
            val newQuantity : Int = maxOf(a = currentQuantity - 1 , b = 0)
            if (newQuantity > 0) {
                updateItemQuantity(cartItem = cartItem , newQuantity = newQuantity)
            }
            else {
                _uiState.update { currentState ->
                    currentState.copy(
                        openDeleteDialog = true , currentCartItemForDeletion = cartItem
                    )
                }
            }
        }
    }

    fun getQuantityStateForItem(cartItem : ShoppingCartItemsTable) : Int {
        return _uiState.value.itemQuantities[cartItem.itemId] ?: cartItem.quantity
    }

    private fun updateItemQuantity(cartItem : ShoppingCartItemsTable , newQuantity : Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            cartItem.quantity = newQuantity
            repository.updateCartItemRepository(cartItem = cartItem) {
                _uiState.update { currentState ->
                    val updatedQuantities : MutableMap<Int , Int> = currentState.itemQuantities.toMutableMap()
                    updatedQuantities[cartItem.itemId] = newQuantity
                    return@update currentState.copy(itemQuantities = updatedQuantities)
                }
                calculateTotalPrice()
            }
        }
    }

    fun editCartItem(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.updateCartItemRepository(cartItem = cartItem) {
                _uiState.update { currentState ->
                    val updatedCartItems = currentState.cartItems.map { item ->
                        if (item.itemId == cartItem.itemId) cartItem else item
                    }
                    currentState.copy(cartItems = updatedCartItems)
                }
                calculateTotalPrice()
            }
        }
    }

    fun deleteCartItem(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteCartItemRepository(cartItem = cartItem) {
                _uiState.update { currentState ->
                    val updatedItems : List<ShoppingCartItemsTable> = currentState.cartItems.filter { it != cartItem }
                    return@update currentState.copy(cartItems = updatedItems)
                }
                calculateTotalPrice()
            }
        }
    }

    fun onItemCheckedChange(cartItem : ShoppingCartItemsTable , isChecked : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val updatedCartItems : List<ShoppingCartItemsTable> = _uiState.value.cartItems.map { item ->
                if (item.itemId == cartItem.itemId) item.copy(isChecked = isChecked) else item
            }
            _uiState.value = _uiState.value.copy(cartItems = updatedCartItems)
            repository.updateCartItemRepository(cartItem = cartItem.copy(isChecked = isChecked)) { }
        }
    }

    fun saveCartItems() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val cartItems : List<ShoppingCartItemsTable> = uiState.value.cartItems
            cartItems.forEach { cartItem ->
                repository.saveCartItemsRepository(cartItem = cartItem) { }
            }
        }
    }

    private fun calculateTotalPrice() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val total = uiState.value.cartItems.sumOf { item -> item.price.toDouble() * item.quantity }
            _uiState.update { currentState ->
                currentState.copy(totalPrice = total)
            }
        }
    }

    fun toggleOpenDialog() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(openDialog = ! currentState.openDialog)
            }
        }
    }

    fun toggleDeleteDialog(cartItem : ShoppingCartItemsTable?) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(
                    openDeleteDialog = cartItem != null , currentCartItemForDeletion = cartItem
                )
            }
        }
    }

    fun toggleEditDialog(cartItem : ShoppingCartItemsTable?) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(
                    openEditDialog = cartItem != null , currentCartItemForEdit = cartItem
                )
            }
        }
    }

    fun shareCart(context : Context , cartId : Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.generateCartShareLinkRepository(cartId = cartId) { shareLink ->
                shareLink?.let {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT , it)
                    }
                    context.startActivity(Intent.createChooser(intent , "Share Cart"))
                }
            }
        }
    }
}