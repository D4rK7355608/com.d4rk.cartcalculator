package com.d4rk.cartcalculator.app.cart.details.ui

import androidx.annotation.StringRes
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.applyResult
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.dismissSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.details.domain.actions.CartAction
import com.d4rk.cartcalculator.app.cart.details.domain.actions.CartEvent
import com.d4rk.cartcalculator.app.cart.details.domain.model.ui.UiCartScreen
import com.d4rk.cartcalculator.app.cart.details.domain.usecases.AddCartItemUseCase
import com.d4rk.cartcalculator.app.cart.details.domain.usecases.DeleteCartItemUseCase
import com.d4rk.cartcalculator.app.cart.details.domain.usecases.LoadCartUseCase
import com.d4rk.cartcalculator.app.cart.details.domain.usecases.UpdateCartItemUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModel(
    private val loadCartUseCase : LoadCartUseCase ,
    private val addCartItemUseCase : AddCartItemUseCase ,
    private val updateCartItemUseCase : UpdateCartItemUseCase ,
    private val deleteCartItemUseCase : DeleteCartItemUseCase ,
    private val generateCartShareLinkUseCase : GenerateCartShareLinkUseCase ,
    internal val dataStore : DataStore ,
    private val dispatcherProvider : DispatcherProvider
) : ScreenViewModel<UiCartScreen , CartEvent , CartAction>(
    initialState = UiStateScreen(data = UiCartScreen())
) {

    fun getQuantityStateForItem(item : ShoppingCartItemsTable) : Int = item.quantity

    override fun onEvent(event : CartEvent) {
        when (event) {
            is CartEvent.LoadCart -> loadCart(event.cartId)
            is CartEvent.AddCartItem -> addCartItem(event.cartId , event.item)
            is CartEvent.UpdateCartItem -> updateCartItem(event.item)
            is CartEvent.DeleteCartItem -> deleteCartItem(event.item)
            is CartEvent.GenerateCartShareLink -> generateCartShareLink(event.cartId)
            is CartEvent.DecreaseQuantity -> decreaseQuantity(event.item)
            is CartEvent.IncreaseQuantity -> increaseQuantity(event.item)
            is CartEvent.OpenNewCartItemDialog -> updateUi { copy(openDialog = event.isOpen) }
            is CartEvent.OpenEditDialog -> updateUi {
                copy(openEditDialog = event.item != null , currentCartItemForEdit = event.item)
            }

            is CartEvent.OpenDeleteDialog -> updateUi {
                copy(openDeleteDialog = event.item != null , currentCartItemForDeletion = event.item)
            }

            is CartEvent.ItemCheckedChange -> updateItemChecked(event.item , event.isChecked)
            is CartEvent.DismissSnackbar -> screenState.dismissSnackbar()
            is CartEvent.OpenClearAllDialog -> updateUi { copy(openClearAllDialog = event.isOpen) }
            is CartEvent.ClearAllItems -> clearAllItems()
        }
    }

    private fun loadCart(cartId : Int) {
        launch(dispatcherProvider.io) {
            val currency = dataStore.getCurrency().firstOrNull().orEmpty()

            loadCartUseCase(cartId).flowOn(dispatcherProvider.default).collect { result ->
                screenState.applyResult(result , UiTextHelper.StringResource(R.string.failed_to_load_cart)) { (cart , items) , current ->
                    current.copy(cart = cart , cartItems = items , selectedCurrency = currency)
                }

                if (result is DataState.Success) {
                    if (result.data.second.isEmpty()) screenState.updateState(ScreenState.NoData())
                    calculateTotalPrice()
                }
            }
        }
    }

    private fun addCartItem(cartId : Int , item : ShoppingCartItemsTable) {
        launch(context = dispatcherProvider.io) {
            val itemWithCart = item.copy(cartId = cartId)
            addCartItemUseCase(param = itemWithCart).flowOn(dispatcherProvider.default).collect { result : DataState<ShoppingCartItemsTable , Errors> ->
                screenState.applyResult(result = result , errorMessage = UiTextHelper.StringResource(R.string.failed_to_add_item)) { newItem : ShoppingCartItemsTable, current : UiCartScreen ->
                    current.copy(cartItems = current.cartItems + newItem)
                }
                if (result is DataState.Success) {
                    calculateTotalPrice()
                    postSnackbar(resId = R.string.item_added_successfully , isError = false)
                }
            }
        }
    }

    private fun updateCartItem(item : ShoppingCartItemsTable) {
        launch(context = dispatcherProvider.io) {
            updateCartItemUseCase(param = item).flowOn(context = dispatcherProvider.default).collect { result : DataState<ShoppingCartItemsTable , Errors> ->
                screenState.applyResult(result = result) { updatedItem : ShoppingCartItemsTable , current : UiCartScreen ->
                    current.copy(cartItems = current.cartItems.map {
                        if (it.itemId == updatedItem.itemId) updatedItem else it
                    })
                }
                if (result is DataState.Success) calculateTotalPrice()
            }
        }
    }

    private fun deleteCartItem(item : ShoppingCartItemsTable) {
        launch(dispatcherProvider.io) {
            deleteCartItemUseCase(param = item).collect { result : DataState<Unit , Errors> ->
                if (result is DataState.Success) {
                    updateUi {
                        val updatedItems = cartItems.filter { it.itemId != item.itemId }
                        copy(
                            cartItems = updatedItems ,
                            totalPrice = updatedItems.sumOf { it.price.toDouble() * it.quantity }
                        )
                    }
                    postSnackbar(resId = R.string.item_removed_successfully , isError = false)
                    checkForEmptyItems()
                }
            }
        }
    }

    private fun generateCartShareLink(cartId : Int) {
        launch(context = dispatcherProvider.io) {
            generateCartShareLinkUseCase(cartId).flowOn(context = dispatcherProvider.default).collect { result : DataState<String , Errors> ->
                screenState.applyResult(result = result) { link : String, current : UiCartScreen ->
                    current.copy(shareCartLink = link)
                }
            }
        }
    }

    private fun decreaseQuantity(item : ShoppingCartItemsTable) {
        launch {
            if (item.quantity <= 1) {
                onEvent(event = CartEvent.OpenDeleteDialog(item = item))
            }
            else {
                changeQuantity(item = item , change = - 1)
            }
        }
    }

    private fun increaseQuantity(item : ShoppingCartItemsTable) {
        launch {
            changeQuantity(item = item , change = 1)
        }
    }

    private fun changeQuantity(item : ShoppingCartItemsTable , change : Int) {
        val updatedItem : ShoppingCartItemsTable = item.copy(quantity = item.quantity + change)
        updateUi {
            copy(cartItems = cartItems.map {
                if (it.itemId == item.itemId) updatedItem else it
            })
        }
        updateCartItem(item = updatedItem)
    }

    private fun clearAllItems() {
        launch(context = dispatcherProvider.io) {
            val itemsToDelete : List<ShoppingCartItemsTable> = screenData?.cartItems ?: return@launch
            itemsToDelete.forEach { deleteCartItem(item = it) }
            updateUi { copy(openClearAllDialog = false) }
        }
    }

    fun areAllItemsChecked() : Boolean {
        val items : List<ShoppingCartItemsTable> = screenData?.cartItems.orEmpty()
        return items.isNotEmpty() && items.all { it.isChecked }
    }

    private fun updateItemChecked(item : ShoppingCartItemsTable , isChecked : Boolean) {
        launch {
            val updatedItem : ShoppingCartItemsTable = item.copy(isChecked = isChecked)
            updateUi {
                copy(cartItems = cartItems.map {
                    if (it.itemId == item.itemId) updatedItem else it
                })
            }
            updateCartItem(item = updatedItem)
        }
    }

    private fun calculateTotalPrice() {
        launch(context = dispatcherProvider.default) {
            val total : Double = screenData?.cartItems?.sumOf { it.price.toDouble() * it.quantity } ?: 0.0
            updateUi { copy(totalPrice = total) }
        }
    }

    private fun checkForEmptyItems() {
        launch {
            val isEmpty : Boolean = screenData?.cartItems?.isEmpty() != false
            screenState.updateState(newValues = if (isEmpty) ScreenState.NoData() else ScreenState.Success())
        }
    }

    private fun postSnackbar(@StringRes resId : Int , isError : Boolean) {
        launch {
            screenState.showSnackbar(snackbar = UiSnackbar(message = UiTextHelper.StringResource(resourceId = resId) , isError = isError , timeStamp = System.currentTimeMillis() , type = ScreenMessageType.SNACKBAR))
        }
    }

    internal inline fun updateUi(crossinline transform : UiCartScreen.() -> UiCartScreen) {
        launch {
            screenState.updateData(newState = screenState.value.screenState) { current : UiCartScreen ->
                transform(current)
            }
        }
    }
}