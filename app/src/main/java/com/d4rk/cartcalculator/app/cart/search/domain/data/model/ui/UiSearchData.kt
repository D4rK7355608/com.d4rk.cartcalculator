package com.d4rk.cartcalculator.app.cart.search.domain.data.model.ui

import com.d4rk.cartcalculator.app.cart.list.domain.model.UiCartListWithDetails // FIXME ADD CLASS

data class UiSearchData(
    val currentQuery: String = "",
    val uiEventsWithDetails: UiCartListWithDetails = UiCartListWithDetails(),
    val isLoading: Boolean = false,
    val initialQueryProcessed: Boolean = false
)