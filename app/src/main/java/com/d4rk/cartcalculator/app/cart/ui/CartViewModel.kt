package com.d4rk.cartcalculator.app.cart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cartcalculator.app.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.cart.domain.usecases.AddCartItemUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.DeleteCartItemUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.LoadCartUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.UpdateCartItemUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val loadCartUseCase : LoadCartUseCase , private val addCartItemUseCase : AddCartItemUseCase , private val updateCartItemUseCase : UpdateCartItemUseCase , private val deleteCartItemUseCase : DeleteCartItemUseCase , private val generateCartShareLinkUseCase : GenerateCartShareLinkUseCase , private val dispatcherProvider : DispatcherProvider) : ViewModel() {

    private val _screenState : MutableStateFlow<UiStateScreen<UiCartScreen>> = MutableStateFlow(value = UiStateScreen(data = UiCartScreen()))
    val screenState : StateFlow<UiStateScreen<UiCartScreen>> = _screenState.asStateFlow()

    fun getQuantityStateForItem(item : ShoppingCartItemsTable) : Int = item.quantity

    fun sendEvent(event : CartAction) {
        when (event) {
            is CartAction.LoadCart -> loadCart(cartId = event.cartId)
            is CartAction.AddCartItem -> addCartItem(cartId = event.cartId , cartItem = event.item)
            is CartAction.UpdateCartItem -> updateCartItem(cartItem = event.item)
            is CartAction.DeleteCartItem -> deleteCartItem(cartItem = event.item)
            is CartAction.GenerateCartShareLink -> generateCartShareLink(cartId = event.cartId)
            is CartAction.DecreaseQuantity -> decreaseQuantity(item = event.item)
            is CartAction.IncreaseQuantity -> increaseQuantity(item = event.item)
            is CartAction.OpenNewCartItemDialog -> openNewCartItemDialog(isOpen = event.isOpen)
            is CartAction.OpenEditDialog -> openEditDialog(item = event.item)
            is CartAction.OpenDeleteDialog -> openDeleteDialog(item = event.item)
            is CartAction.ItemCheckedChange -> updateItemChecked(item = event.item , isChecked = event.isChecked)
        }
    }

    private fun loadCart(cartId : Int) {
        viewModelScope.launch {
            loadCartUseCase.invoke(param = cartId).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val (cart: ShoppingCartTable, items: List<ShoppingCartItemsTable>) = result.data
                        println("Cart loaded: ${cart.cartId}, Items: ${items.size}") // Debugging log

                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            currentData.copy(cart = cart, cartItems = items)
                        }

                        if (items.isEmpty()) {
                            _screenState.updateState(newValues = ScreenState.NoData())
                        }

                        calculateTotalPrice()
                    }

                    is DataState.Error -> {
                        _screenState.updateState(newValues = ScreenState.NoData())
                    }

                    is DataState.Loading -> {
                        _screenState.updateState(newValues = ScreenState.IsLoading())
                    }

                    else -> {}
                }
            }
        }
    }

    private fun addCartItem(cartId : Int , cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch {
            val itemWithCart : ShoppingCartItemsTable = cartItem.copy(cartId = cartId)
            addCartItemUseCase.invoke(param = itemWithCart).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val newItem = result.data.copy(cartId = cartId) // Ensure correct cart ID
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            currentData.copy(cartItems = currentData.cartItems + newItem)
                        }
                        calculateTotalPrice()
                    }

                    is DataState.Error -> {
                        _screenState.showSnackbar(UiSnackbar(message = UiTextHelper.DynamicString(content = "Failed to add item") , isError = true))
                    }

                    is DataState.Loading -> {}
                    else -> {}
                }
            }
        }
    }

    private fun updateCartItem(cartItem : ShoppingCartItemsTable) {
        viewModelScope.launch {
            updateCartItemUseCase.invoke(param = cartItem).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            val updatedItems : List<ShoppingCartItemsTable> = currentData.cartItems.map { item ->
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
            deleteCartItemUseCase.invoke(param = cartItem).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
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
            generateCartShareLinkUseCase.invoke(param = cartId).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
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

    private fun openNewCartItemDialog(isOpen : Boolean) {
        viewModelScope.launch {
            _screenState.updateData(newDataState = _screenState.value.screenState) { it.copy(openDialog = isOpen) }
        }
    }

    private fun openEditDialog(item : ShoppingCartItemsTable?) {
        viewModelScope.launch {
            _screenState.updateData(newDataState = _screenState.value.screenState) { it.copy(openEditDialog = item != null , currentCartItemForEdit = item) }
        }
    }

    private fun openDeleteDialog(item : ShoppingCartItemsTable?) {
        viewModelScope.launch {
            _screenState.updateData(newDataState = _screenState.value.screenState) { it.copy(openDeleteDialog = item != null , currentCartItemForDeletion = item) }
        }
    }

    private fun calculateTotalPrice() {
        viewModelScope.launch {
            val total : Double = _screenState.value.data?.cartItems?.sumOf { it.price.toDouble() * it.quantity } ?: 0.0
            _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
                currentData.copy(totalPrice = total)
            }
        }
    }

    private fun decreaseQuantity(item : ShoppingCartItemsTable) {
        if (item.quantity <= 1) {
            openDeleteDialog(item = item)
        }
        else {
            changeQuantity(item = item , change = - 1)
        }
    }

    private fun increaseQuantity(item : ShoppingCartItemsTable) {
        changeQuantity(item = item , change = 1)
    }

    private fun changeQuantity(item : ShoppingCartItemsTable , change : Int) {
        viewModelScope.launch {
            val updatedItem : ShoppingCartItemsTable = item.copy(quantity = item.quantity + change)
            _screenState.updateData(newDataState = ScreenState.Success()) { currentData : UiCartScreen ->
                val updatedItems : List<ShoppingCartItemsTable> = currentData.cartItems.map { existingItem : ShoppingCartItemsTable ->
                    if (existingItem.itemId == item.itemId) existingItem.copy(quantity = existingItem.quantity + change) else existingItem
                }
                currentData.copy(cartItems = updatedItems)
            }
            updateCartItem(cartItem = updatedItem)
        }
    }

    private fun updateItemChecked(item : ShoppingCartItemsTable , isChecked : Boolean) {
        viewModelScope.launch {
            _screenState.updateData(newDataState = _screenState.value.screenState) { currentState : UiCartScreen ->
                val checkedCartItems : List<ShoppingCartItemsTable> = currentState.cartItems.map { existingItem : ShoppingCartItemsTable ->
                    if (existingItem.itemId == item.itemId) existingItem.copy(isChecked = isChecked) else existingItem
                }
                currentState.copy(cartItems = checkedCartItems)
            }
            updateCartItem(cartItem = item.copy(isChecked = isChecked))
        }
    }
}