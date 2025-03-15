package com.d4rk.cartcalculator.app.cart.domain.actions

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

sealed class CartAction {
    data class LoadCart(val cartId: Int) : CartAction()
    data class AddCartItem(val cartId: Int, val item: ShoppingCartItemsTable) : CartAction()
    data class UpdateCartItem(val item: ShoppingCartItemsTable) : CartAction()
    data class DeleteCartItem(val item: ShoppingCartItemsTable) : CartAction()
    data class GenerateCartShareLink(val cartId: Int) : CartAction()
    data class OpenNewCartItemDialog(val isOpen: Boolean) : CartAction()
    data class DecreaseQuantity(val item: ShoppingCartItemsTable) : CartAction()
    data class IncreaseQuantity(val item: ShoppingCartItemsTable) : CartAction()
    data class OpenEditDialog(val item: ShoppingCartItemsTable?) : CartAction()
    data class OpenDeleteDialog(val item: ShoppingCartItemsTable?) : CartAction()
    data class ItemCheckedChange(val item: ShoppingCartItemsTable, val isChecked: Boolean) : CartAction()
}
