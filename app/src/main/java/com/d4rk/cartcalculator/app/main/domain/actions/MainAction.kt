package com.d4rk.cartcalculator.app.main.domain.actions

sealed class MainAction {
    data object CheckForUpdates : MainAction()
}