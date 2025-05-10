package com.d4rk.cartcalculator.app.cart.details.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

sealed class CartEvent : UiEvent {
    data class LoadCart(val cartId : Int) : CartEvent()
    data class AddCartItem(val cartId : Int , val item : ShoppingCartItemsTable) : CartEvent()
    data class UpdateCartItem(val item : ShoppingCartItemsTable) : CartEvent()
    data class DeleteCartItem(val item : ShoppingCartItemsTable) : CartEvent()
    data class GenerateCartShareLink(val cartId : Int) : CartEvent()
    data class DecreaseQuantity(val item : ShoppingCartItemsTable) : CartEvent()
    data class IncreaseQuantity(val item : ShoppingCartItemsTable) : CartEvent()
    data class ItemCheckedChange(val item : ShoppingCartItemsTable , val isChecked : Boolean) : CartEvent()
    data class OpenNewCartItemDialog(val isOpen : Boolean) : CartEvent()
    data class OpenEditDialog(val item : ShoppingCartItemsTable?) : CartEvent()
    data class OpenDeleteDialog(val item : ShoppingCartItemsTable?) : CartEvent()
    data object DismissSnackbar : CartEvent()
    data class OpenClearAllDialog(val isOpen : Boolean) : CartEvent()
    data object ClearAllItems : CartEvent()
}