package com.d4rk.cartcalculator.app.cart.search.domain.data.model.ui

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

/**
 * UI model used by the search screen.
 */
data class UiSearchData(
    val currentQuery: String = "",
    val carts: MutableList<ShoppingCartTable> = mutableListOf(),
    val isLoading: Boolean = false,
    val initialQueryProcessed: Boolean = false
)