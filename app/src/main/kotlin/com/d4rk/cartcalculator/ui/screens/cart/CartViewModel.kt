package com.d4rk.cartcalculator.ui.screens.cart

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.data.model.ui.screens.UiCartModel
import com.d4rk.cartcalculator.ui.screens.cart.repository.CartRepository
import com.d4rk.cartcalculator.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(application : Application) : BaseViewModel(application) {

    private val repository = CartRepository()

    private val _uiState = MutableStateFlow(UiCartModel())
    val uiState : StateFlow<UiCartModel> = _uiState

    fun loadCart(cartId : Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            val cart = repository.loadCartIdImplementation(cartId = cartId)
            repository.loadCartItemsRepository(cartId = cartId) { items ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cartItems = items , cart = cart
                    )
                }
                calculateTotalPrice()
            }
            hideLoading()
        }
    }

    fun loadSelectedCurrency(dataStore : DataStore) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(
                    selectedCurrency = dataStore.getCurrency().firstOrNull() ?: ""
                )
            }
        }
    }

    fun addCartItem(cartId : Int , cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            cartItem.cartId = cartId
            repository.addCartItemRepository(cartItem) { newItem ->
                _uiState.update { currentState ->
                    val cartItems = currentState.cartItems + newItem
                    currentState.copy(cartItems = cartItems)
                }
                calculateTotalPrice()
            }
        }
    }

    fun increaseQuantity(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val currentQuantity = getQuantityStateForItem(cartItem = cartItem)
            updateItemQuantity(cartItem = cartItem , newQuantity = currentQuantity + 1)
        }
    }

    fun decreaseQuantity(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val currentQuantity = getQuantityStateForItem(cartItem = cartItem)
            val newQuantity = maxOf(a = currentQuantity - 1 , b = 0)
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
        viewModelScope.launch(coroutineExceptionHandler) {
            cartItem.quantity = newQuantity
            repository.updateCartItemRepository(cartItem = cartItem) {
                _uiState.update { currentState ->
                    val updatedQuantities = currentState.itemQuantities.toMutableMap()
                    updatedQuantities[cartItem.itemId] = newQuantity
                    return@update currentState.copy(itemQuantities = updatedQuantities)
                }
                calculateTotalPrice()
            }
        }
    }

    fun deleteCartItem(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteCartItemRepository(cartItem) {
                _uiState.update { currentState ->
                    val updatedItems = currentState.cartItems.filter { it != cartItem }
                    return@update currentState.copy(cartItems = updatedItems)
                }
                calculateTotalPrice()
            }
        }
    }

    fun onItemCheckedChange(cartItem : ShoppingCartItemsTable , isChecked : Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val updatedCartItems = _uiState.value.cartItems.map { item ->
                if (item.itemId == cartItem.itemId) item.copy(isChecked = isChecked) else item
            }
            _uiState.value = _uiState.value.copy(cartItems = updatedCartItems)
            repository.updateCartItemRepository(cartItem.copy(isChecked = isChecked)) { }
        }
    }

    fun saveCartItems() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val cartItems = uiState.value.cartItems
            cartItems.forEach { cartItem ->
                repository.saveCartItemsRepository(cartItem) { }
            }
        }
    }

    private fun calculateTotalPrice() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val total =
                    uiState.value.cartItems.sumOf { item -> item.price.toDouble() * item.quantity }
            _uiState.update { currentState ->
                currentState.copy(totalPrice = total)
            }
        }
    }

    fun toggleOpenDialog() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(openDialog = ! currentState.openDialog)
            }
        }
    }

    fun toggleDeleteDialog(cartItem : ShoppingCartItemsTable?) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(
                    openDeleteDialog = cartItem != null , currentCartItemForDeletion = cartItem
                )
            }
        }
    }
}