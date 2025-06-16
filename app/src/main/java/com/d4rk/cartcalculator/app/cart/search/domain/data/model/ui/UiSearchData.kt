package com.d4rk.cartcalculator.app.cart.search.domain.data.model.ui

import com.d4rk.android.apps.weddix.app.events.list.domain.model.UiEventWithDetails

data class UiSearchData(
    val currentQuery: String = "",
    val uiEventsWithDetails: UiEventWithDetails = UiEventWithDetails(),
    val isLoading: Boolean = false,
    val initialQueryProcessed: Boolean = false
)