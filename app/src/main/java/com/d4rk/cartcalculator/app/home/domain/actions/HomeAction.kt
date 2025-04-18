package com.d4rk.cartcalculator.app.home.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper

sealed class HomeAction : ActionEvent {
    data class ShowSnackbar(val message : UiTextHelper , val isError : Boolean) : HomeAction()
    data class ShareCart(val link : String) : HomeAction()
}