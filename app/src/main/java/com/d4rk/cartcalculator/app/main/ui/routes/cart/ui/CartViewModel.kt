package com.d4rk.cartcalculator.app.main.ui.routes.cart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases.AddCartItemUseCase
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases.DeleteCartItemUseCase
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases.LoadCartUseCase
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases.UpdateCartItemUseCase
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases.GenerateCartShareLinkUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.di.DispatcherProvider
import com.d4rk.cartcalculator.core.domain.model.network.DataState
import com.d4rk.cartcalculator.core.domain.model.ui.ScreenState
import com.d4rk.cartcalculator.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.core.domain.model.ui.updateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CartViewModel(
    private val loadCartUseCase : LoadCartUseCase ,
    private val addCartItemUseCase : AddCartItemUseCase ,
    private val updateCartItemUseCase : UpdateCartItemUseCase ,
    private val deleteCartItemUseCase : DeleteCartItemUseCase ,
    private val generateCartShareLinkUseCase : GenerateCartShareLinkUseCase ,
    private val dispatcherProvider : DispatcherProvider
) : ViewModel() {

    private val _screenState : MutableStateFlow<UiStateScreen<UiCartScreen>> = MutableStateFlow(value = UiStateScreen(data = UiCartScreen()))
    val screenState : StateFlow<UiStateScreen<UiCartScreen>> = _screenState.asStateFlow()

    // Returns the current quantity for an item (could be extended to use a separate state)
    fun getQuantityStateForItem(item : ShoppingCartItemsTable) : Int = item.quantity

    fun sendEvent(event : CartAction) {
        when (event) {
            is CartAction.LoadCart -> loadCart(event.cartId)
            is CartAction.AddCartItem -> addCartItem(event.cartId , event.item)
            is CartAction.UpdateCartItem -> updateCartItem(event.item)
            is CartAction.DeleteCartItem -> deleteCartItem(event.item)
            is CartAction.GenerateCartShareLink -> generateCartShareLink(event.cartId)
            is CartAction.DecreaseQuantity -> decreaseQuantity(event.item)
            is CartAction.IncreaseQuantity -> increaseQuantity(event.item)
            is CartAction.OpenNewCartItemDialog -> {
                // Open the dialog to add a new cart item
            }

            is CartAction.OpenEditDialog -> {
                // Open dialog to edit this item
            }

            is CartAction.OpenDeleteDialog -> {
                // Open dialog to confirm deletion of this item
            }

            is CartAction.ItemCheckedChange -> {
                // Update the checked status of the item
                val updatedItem = event.item.copy(isChecked = event.isChecked)
                sendEvent(CartAction.UpdateCartItem(updatedItem))
            }
        }
    }

    private fun loadCart(cartId : Int) {
        viewModelScope.launch {
            loadCartUseCase.invoke(cartId).flowOn(dispatcherProvider.io).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val (cart , items) = result.data
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            currentData.copy(cart = cart , cartItems = items)
                        }
                        calculateTotalPrice()
                    }

                    is DataState.Error -> {
                        // Handle error (e.g., show snackbar)
                    }

                    is DataState.Loading -> {
                        // Optionally update UI state to loading
                    }

                    else -> {}
                }
            }
        }
    }

    private fun addCartItem(cartId : Int , cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch {
            val itemWithCart = cartItem.copy(cartId = cartId)
            addCartItemUseCase.invoke(itemWithCart).flowOn(dispatcherProvider.io).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            currentData.copy(cartItems = currentData.cartItems + result.data)
                        }
                        calculateTotalPrice()
                    }

                    is DataState.Error -> {
                        // Handle error (e.g., show snackbar)
                    }

                    is DataState.Loading -> {}
                    else -> {}
                }
            }
        }
    }

    private fun updateCartItem(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch {
            updateCartItemUseCase.invoke(cartItem).flowOn(dispatcherProvider.io).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            val updatedItems = currentData.cartItems.map { item ->
                                if (item.itemId == cartItem.itemId) result.data else item
                            }
                            currentData.copy(cartItems = updatedItems)
                        }
                        calculateTotalPrice()
                    }

                    is DataState.Error -> {
                        // Handle error
                    }

                    is DataState.Loading -> {}
                    else -> {}
                }
            }
        }
    }

    private fun deleteCartItem(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch {
            deleteCartItemUseCase.invoke(cartItem).flowOn(dispatcherProvider.io).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            currentData.copy(cartItems = currentData.cartItems.filter { it.itemId != cartItem.itemId })
                        }
                        calculateTotalPrice()
                    }

                    is DataState.Error -> {
                        // Handle error
                    }

                    is DataState.Loading -> {}
                    else -> {}
                }
            }
        }
    }

    private fun generateCartShareLink(cartId : Int) {
        viewModelScope.launch {
            generateCartShareLinkUseCase.invoke(cartId).flowOn(dispatcherProvider.io).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            currentData.copy(shareCartLink = result.data)
                        }
                    }

                    is DataState.Error -> {
                        // Handle error
                    }

                    is DataState.Loading -> {}
                    else -> {}
                }
            }
        }
    }

    private fun calculateTotalPrice() {
        viewModelScope.launch {
            val total = _screenState.value.data?.cartItems?.sumOf { it.price.toDouble() * it.quantity } ?: 0.0
            _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
                currentData.copy(totalPrice = total)
            }
        }
    }

    private fun decreaseQuantity(item : ShoppingCartItemsTable) {
        val updatedItem = item.copy(quantity = item.quantity - 1)
        sendEvent(CartAction.UpdateCartItem(updatedItem))
    }

    private fun increaseQuantity(item : ShoppingCartItemsTable) {
        val updatedItem = item.copy(quantity = item.quantity + 1)
        sendEvent(CartAction.UpdateCartItem(updatedItem))
    }
}