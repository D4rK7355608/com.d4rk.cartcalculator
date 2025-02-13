package com.d4rk.cartcalculator.ui.components.navigation

import android.content.Context
import androidx.compose.material3.DrawerState
import com.d4rk.android.libs.apptoolkit.data.model.ui.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper
import com.d4rk.cartcalculator.ui.screens.help.HelpActivity
import com.d4rk.cartcalculator.ui.screens.settings.SettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun handleNavigationItemClick(
    context : Context , item : NavigationDrawerItem , drawerState : DrawerState , coroutineScope : CoroutineScope
) {
    when (item.title) {
        com.d4rk.android.libs.apptoolkit.R.string.settings -> IntentsHelper.openActivity(
            context = context , activityClass = SettingsActivity::class.java
        )

        com.d4rk.android.libs.apptoolkit.R.string.help_and_feedback -> IntentsHelper.openActivity(
            context = context , activityClass = HelpActivity::class.java
        )

        com.d4rk.android.libs.apptoolkit.R.string.updates -> IntentsHelper.openUrl(
            context = context , url = "https://github.com/D4rK7355608/${context.packageName}/blob/master/CHANGELOG.md"
        )

        com.d4rk.android.libs.apptoolkit.R.string.share -> IntentsHelper.shareApp(
            context = context , shareMessageFormat = com.d4rk.android.libs.apptoolkit.R.string.summary_share_message
        )
    }
    coroutineScope.launch { drawerState.close() }
}