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
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cartcalculator.app.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.cart.domain.usecases.AddCartItemUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.DeleteCartItemUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.LoadCartUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.UpdateCartItemUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val loadCartUseCase : LoadCartUseCase ,
    private val addCartItemUseCase : AddCartItemUseCase ,
    private val updateCartItemUseCase : UpdateCartItemUseCase ,
    private val deleteCartItemUseCase : DeleteCartItemUseCase ,
    private val generateCartShareLinkUseCase : GenerateCartShareLinkUseCase ,
    private val dataStore : DataStore ,
    private val dispatcherProvider : DispatcherProvider
) : ViewModel() {

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
            is CartAction.ShowSnackbar -> showSnackbar(message = event.message , isError = event.isError)
            is CartAction.DismissSnackbar -> dismissSnackbar()
        }
    }

    private fun loadCart(cartId : Int) {
        viewModelScope.launch {
            loadCartUseCase.invoke(param = cartId).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val (cart : ShoppingCartTable , items : List<ShoppingCartItemsTable>) = result.data
                        val currency = dataStore.getCurrency().firstOrNull().orEmpty()
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData : UiCartScreen ->
                            currentData.copy(
                                cart = cart , cartItems = items , selectedCurrency = currency
                            )
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
                        val newItem = result.data.copy(cartId = cartId)
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData : UiCartScreen ->
                            currentData.copy(cartItems = currentData.cartItems + newItem)
                        }
                        calculateTotalPrice()
                        sendEvent(CartAction.ShowSnackbar("Item added successfully!" , false))
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
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData : UiCartScreen ->
                            val updatedItems : List<ShoppingCartItemsTable> = currentData.cartItems.map { item : ShoppingCartItemsTable ->
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
                        val updatedItems : List<ShoppingCartItemsTable> = _screenState.value.data?.cartItems?.filter { it.itemId != cartItem.itemId }.orEmpty()
                        if (updatedItems.isEmpty()) {
                            _screenState.updateData(newDataState = ScreenState.NoData()) { currentData : UiCartScreen ->
                                currentData.copy(cartItems = emptyList())
                            }
                        }
                        else {
                            _screenState.updateData(newDataState = ScreenState.Success()) { currentData : UiCartScreen ->
                                currentData.copy(cartItems = updatedItems)
                            }
                        }

                        calculateTotalPrice()
                        sendEvent(CartAction.ShowSnackbar("Item removed successfully!" , false))
                    }

                    is DataState.Error -> {

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

    private fun showSnackbar(message : String , isError : Boolean) {
        _screenState.showSnackbar(
            UiSnackbar(
                type = ScreenMessageType.SNACKBAR , message = UiTextHelper.DynamicString(message) , isError = isError , timeStamp = System.currentTimeMillis()
            )
        )
    }

    private fun dismissSnackbar() {
        _screenState.update { current ->
            current.copy(snackbar = null)
        }
    }
}