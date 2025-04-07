package com.d4rk.cartcalculator.app.home.domain.events

import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper

sealed class HomeUiEvent {
    data class ShowSnackbar(val message : UiTextHelper , val isError : Boolean) : HomeUiEvent()
    data class ShareCart(val link : String) : HomeUiEvent()
}