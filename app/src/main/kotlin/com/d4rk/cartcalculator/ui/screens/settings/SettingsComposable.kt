package com.d4rk.cartcalculator.ui.screens.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SafetyCheck
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.ui.components.preferences.SettingsPreferenceItem
import com.d4rk.cartcalculator.ui.components.navigation.TopAppBarScaffoldWithBackButton
import com.d4rk.cartcalculator.ui.screens.settings.about.AboutSettingsActivity
import com.d4rk.cartcalculator.ui.screens.settings.advanced.AdvancedSettingsActivity
import com.d4rk.cartcalculator.ui.screens.settings.cart.CartSettingsActivity
import com.d4rk.cartcalculator.ui.screens.settings.display.DisplaySettingsActivity
import com.d4rk.cartcalculator.ui.screens.settings.privacy.PrivacySettingsActivity
import com.d4rk.cartcalculator.utils.helpers.IntentsHelper

@Composable
fun SettingsComposable(activity: SettingsActivity) {
    val context: Context = LocalContext.current

    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.settings),
        onBackClicked = { activity.finish() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues),
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                ) {
                    SettingsPreferenceItem(Icons.Outlined.Notifications,
                                           title = stringResource(id = R.string.notifications),
                                           summary = stringResource(id = R.string.summary_preference_settings_notifications),
                                           onClick = {
                                               IntentsHelper.openAppNotificationSettings(context)
                                           })
                    Spacer(modifier = Modifier.height(2.dp))
                    SettingsPreferenceItem(Icons.Outlined.Palette,
                                           title = stringResource(id = R.string.display),
                                           summary = stringResource(id = R.string.summary_preference_settings_display),
                                           onClick = {
                                               IntentsHelper.openActivity(
                                                   context, DisplaySettingsActivity::class.java
                                               )
                                           })
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Column(
                    modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                ) {
                    SettingsPreferenceItem(Icons.Outlined.ShoppingCart,
                                           title = stringResource(id = R.string.cart_settings),
                                           summary = stringResource(id = R.string.summary_preference_settings_cart),
                                           onClick = {
                                               IntentsHelper.openActivity(
                                                   context, CartSettingsActivity::class.java
                                               )
                                           })
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Column(
                    modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                ) {
                    SettingsPreferenceItem(Icons.Outlined.SafetyCheck,
                                           title = stringResource(id = R.string.security_and_privacy),
                                           summary = stringResource(id = R.string.summary_preference_settings_privacy_and_security),
                                           onClick = {
                                               IntentsHelper.openActivity(
                                                   context, PrivacySettingsActivity::class.java
                                               )
                                           })
                    Spacer(modifier = Modifier.height(2.dp))
                    SettingsPreferenceItem(Icons.Outlined.Build,
                                           title = stringResource(id = R.string.advanced),
                                           summary = stringResource(id = R.string.summary_preference_settings_advanced),
                                           onClick = {
                                               IntentsHelper.openActivity(
                                                   context, AdvancedSettingsActivity::class.java
                                               )
                                           })
                    Spacer(modifier = Modifier.height(2.dp))
                    SettingsPreferenceItem(Icons.Outlined.Info,
                                           title = stringResource(id = R.string.about),
                                           summary = stringResource(id = R.string.summary_preference_settings_about),
                                           onClick = {
                                               IntentsHelper.openActivity(
                                                   context, AboutSettingsActivity::class.java
                                               )
                                           })
                }
            }
        }
    }
}