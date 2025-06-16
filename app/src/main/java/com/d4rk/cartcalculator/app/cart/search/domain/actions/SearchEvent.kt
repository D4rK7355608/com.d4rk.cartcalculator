package com.d4rk.cartcalculator.app.cart.search.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent

sealed interface SearchEvent : UiEvent {
    data class UpdateQuery(val query: String) : SearchEvent
    data class SubmitSearch(val query: String) : SearchEvent
    data object ClearSearch : SearchEvent
    data class ProcessInitialQuery(val encodedQuery: String?) : SearchEvent
}
