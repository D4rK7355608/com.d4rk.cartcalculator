package com.d4rk.cartcalculator.app.settings.settings.utils.providers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.d4rk.cartcalculator.app.settings.cart.backup.ui.CartBackupSettingsScreen
import com.d4rk.cartcalculator.app.settings.cart.list.ui.CartSettingsList
import com.d4rk.cartcalculator.app.settings.settings.utils.constants.SettingsConstants

class AppSettingsScreens {
    val customScreens: Map<String, @Composable (PaddingValues) -> Unit> = mapOf(
        SettingsConstants.KEY_SETTINGS_CART to { paddingValues -> CartSettingsList(paddingValues = paddingValues) },
        SettingsConstants.KEY_SETTINGS_BACKUP_CARTS to { paddingValues -> CartBackupSettingsScreen(paddingValues = paddingValues) }
    )
}