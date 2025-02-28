package com.d4rk.cartcalculator.ui.screens.settings.general

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.about.AboutSettingsList
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.advanced.AdvancedSettingsList
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.display.DisplaySettingsList
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.display.theme.ThemeSettingsList
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.privacy.PrivacySettingsList
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.privacy.usage.UsageAndDiagnosticsList
import com.d4rk.cartcalculator.ui.screens.settings.cart.CartSettingsList
import com.d4rk.cartcalculator.utils.providers.AppAboutSettingsProvider
import com.d4rk.cartcalculator.utils.providers.AppAdvancedSettingsProvider
import com.d4rk.cartcalculator.utils.providers.AppDisplaySettingsProvider
import com.d4rk.cartcalculator.utils.providers.AppPrivacySettingsProvider
import com.d4rk.cartcalculator.utils.providers.AppUsageAndDiagnosticsProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    title : String , content : SettingsContent? , onBackClicked : () -> Unit
) {
    LargeTopAppBarWithScaffold(title = title , onBackClicked = onBackClicked) { paddingValues ->
        when (content) {
            SettingsContent.ABOUT -> AboutSettingsList(
                paddingValues = paddingValues , provider = AppAboutSettingsProvider()
            )

            SettingsContent.ADVANCED -> AdvancedSettingsList(
                paddingValues = paddingValues , provider = AppAdvancedSettingsProvider()
            )

            SettingsContent.DISPLAY -> DisplaySettingsList(
                paddingValues = paddingValues , provider = AppDisplaySettingsProvider()
            )

            SettingsContent.CART -> CartSettingsList(
                paddingValues = paddingValues
            )

            SettingsContent.PRIVACY -> PrivacySettingsList(
                paddingValues = paddingValues , provider = AppPrivacySettingsProvider()
            )

            SettingsContent.THEME -> ThemeSettingsList(
                paddingValues = paddingValues
            )

            SettingsContent.USAGE_AND_DIAGNOSTICS -> UsageAndDiagnosticsList(
                paddingValues = paddingValues,
                provider = AppUsageAndDiagnosticsProvider()
            )

            else -> {}
        }
    }
}