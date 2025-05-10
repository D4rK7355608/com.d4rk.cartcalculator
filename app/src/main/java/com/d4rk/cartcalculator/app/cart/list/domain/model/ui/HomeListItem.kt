package com.d4rk.cartcalculator.app.cart.list.domain.model.ui

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

sealed class HomeListItem {
    data class CartItem(val cart : ShoppingCartTable) : HomeListItem()
    object AdItem : HomeListItem()
}