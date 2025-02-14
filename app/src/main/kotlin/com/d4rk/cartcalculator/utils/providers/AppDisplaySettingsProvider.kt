
package com.d4rk.cartcalculator.utils.providers

import android.content.Intent
import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.utils.interfaces.providers.DisplaySettingsProvider
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.ui.components.dialogs.SelectLanguageAlertDialog
import com.d4rk.cartcalculator.ui.screens.settings.general.GeneralSettingsActivity
import com.d4rk.cartcalculator.ui.screens.settings.general.SettingsContent

class AppDisplaySettingsProvider : DisplaySettingsProvider {

    @Composable
    override fun LanguageSelectionDialog(onDismiss : () -> Unit , onLanguageSelected : (String) -> Unit) {
        SelectLanguageAlertDialog(
            dataStore = AppCoreManager.dataStore , onDismiss = onDismiss , onLanguageSelected = onLanguageSelected
        )
    }

    @Composable
    override fun StartupPageDialog(onDismiss : () -> Unit , onStartupSelected : (String) -> Unit) {}

    override fun openThemeSettings() {
        val context : AppCoreManager = AppCoreManager.instance
        val intent : Intent = Intent(context , GeneralSettingsActivity::class.java).apply {
            putExtra("extra_title" , context.getString(com.d4rk.android.libs.apptoolkit.R.string.dark_theme))
            putExtra("extra_content" , SettingsContent.THEME.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}