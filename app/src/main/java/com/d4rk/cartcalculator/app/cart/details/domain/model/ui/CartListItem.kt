package com.d4rk.cartcalculator.app.cart.details.domain.model.ui

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

sealed class CartListItem {
    data class CartItem(val item : ShoppingCartItemsTable) : CartListItem()
    object AdItem : CartListItem()
    data class Header(val label : String) : CartListItem()
}