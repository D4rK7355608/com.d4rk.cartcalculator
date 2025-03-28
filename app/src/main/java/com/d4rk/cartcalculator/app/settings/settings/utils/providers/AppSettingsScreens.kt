package com.d4rk.cartcalculator.app.settings.settings.utils.providers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.d4rk.cartcalculator.app.settings.cart.ui.CartSettingsList

class AppSettingsScreens {
    val customScreens : Map<String , @Composable (PaddingValues) -> Unit> = mapOf(
        "cart" to { paddingValues -> CartSettingsList(paddingValues) })
}
