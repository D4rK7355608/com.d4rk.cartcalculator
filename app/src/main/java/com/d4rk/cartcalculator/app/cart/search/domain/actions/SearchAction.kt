package com.d4rk.cartcalculator.app.cart.search.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent

sealed interface SearchAction : ActionEvent {
    // Example: data class NavigateToDetails(val itemId: String) : SearchAction
}