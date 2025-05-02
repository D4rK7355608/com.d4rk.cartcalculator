package com.d4rk.cartcalculator.app.home.domain.model

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

sealed class HomeListItem {
    data class CartItem(val cart: ShoppingCartTable) : HomeListItem()
    object AdItem : HomeListItem()
}