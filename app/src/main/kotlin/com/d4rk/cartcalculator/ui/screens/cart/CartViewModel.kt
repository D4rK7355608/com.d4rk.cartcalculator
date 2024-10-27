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
            val cart = repository.getCartById(cartId)
            repository.getCartItems(cartId) { items ->
                _uiState.update {
                    it.copy(
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
            val currency = dataStore.getCurrency().firstOrNull() ?: ""
            _uiState.update { it.copy(selectedCurrency = currency) }
        }
    }

    fun addCartItem(cartId : Int , cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            cartItem.cartId = cartId
            repository.addItemToCart(cartItem) { newItem ->
                _uiState.update { currentState ->
                    val updatedItems = currentState.cartItems + newItem
                    currentState.copy(cartItems = updatedItems)
                }
                calculateTotalPrice()
            }
        }
    }

    fun getQuantityStateForItem(cartItem : ShoppingCartItemsTable) : Int {
        return _uiState.value.itemQuantities[cartItem.itemId] ?: cartItem.quantity
    }

    fun increaseQuantity(cartItem : ShoppingCartItemsTable) {
        val currentQuantity = getQuantityStateForItem(cartItem)
        viewModelScope.launch(coroutineExceptionHandler) {
            val newQuantity = currentQuantity + 1
            updateItemQuantity(cartItem , newQuantity)
        }
    }

    fun decreaseQuantity(cartItem : ShoppingCartItemsTable) {
        val currentQuantity = getQuantityStateForItem(cartItem)
        viewModelScope.launch(coroutineExceptionHandler) {
            val newQuantity = maxOf(currentQuantity - 1 , 0)
            if (newQuantity > 0) {
                updateItemQuantity(cartItem , newQuantity)
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

    private fun updateItemQuantity(cartItem : ShoppingCartItemsTable , newQuantity : Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            cartItem.quantity = newQuantity
            repository.updateCartItem(cartItem) {
                _uiState.update { currentState ->
                    val updatedQuantities = currentState.itemQuantities.toMutableMap()
                    updatedQuantities[cartItem.itemId] = newQuantity
                    currentState.copy(itemQuantities = updatedQuantities)
                }
                calculateTotalPrice()
            }
        }
    }

    fun deleteCartItem(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteCartItem(cartItem) {
                _uiState.update { currentState ->
                    val updatedItems = currentState.cartItems.filter { it != cartItem }
                    currentState.copy(cartItems = updatedItems)
                }
                calculateTotalPrice()
            }
        }
    }

    fun onItemCheckedChange(cartItem : ShoppingCartItemsTable , isChecked : Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            cartItem.isChecked = isChecked
            repository.updateCartItem(cartItem) { }
        }
    }

    fun saveCartItems() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val cartItems = uiState.value.cartItems
            cartItems.forEach { cartItem ->
                repository.saveCartItems(cartItem) { }
            }
        }
    }

    private fun calculateTotalPrice() {
        val total = uiState.value.cartItems.sumOf { item -> item.price.toDouble() * item.quantity }
        _uiState.update {
            println(
                "Shopping Cart Calculator -> [CartViewModel] UiState updated, items: ${
                    it.copy(
                        totalPrice = total
                    ).cartItems
                }"
            )
            it.copy(totalPrice = total)
        }
    }

    fun toggleOpenDialog() {
        _uiState.update { it.copy(openDialog = ! it.openDialog) }
    }

    fun toggleDeleteDialog(cartItem : ShoppingCartItemsTable?) {
        _uiState.update { currentState ->
            currentState.copy(
                openDeleteDialog = cartItem != null , currentCartItemForDeletion = cartItem
            )
        }
    }
}