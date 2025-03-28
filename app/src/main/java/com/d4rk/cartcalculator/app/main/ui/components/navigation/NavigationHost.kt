package com.d4rk.cartcalculator.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d4rk.android.libs.apptoolkit.app.help.ui.HelpActivity
import com.d4rk.android.libs.apptoolkit.app.settings.settings.ui.SettingsActivity
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.ui.HomeScreen
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel
import com.d4rk.cartcalculator.app.home.ui.components.effects.HomeEventHandler
import com.d4rk.cartcalculator.app.main.utils.constants.NavigationRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavigationHost(navController : NavHostController , snackbarHostState : SnackbarHostState , onFabVisibilityChanged : (Boolean) -> Unit , paddingValues : PaddingValues) {
    NavHost(navController = navController , startDestination = NavigationRoutes.ROUTE_HOME) {
        composable(route = "home") { backStackEntry ->
            val homeViewModel : HomeViewModel = koinViewModel()
            val homeScreenState : UiStateScreen<UiHomeData> by homeViewModel.screenState.collectAsState()

            HomeEventHandler(homeViewModel = homeViewModel , snackbarHostState = snackbarHostState , backStackEntry = backStackEntry , homeScreenState = homeScreenState)

            HomeScreen(paddingValues = paddingValues , viewModel = homeViewModel , onFabVisibilityChanged = onFabVisibilityChanged , snackbarHostState = snackbarHostState , screenState = homeScreenState)
        }
    }
}

fun handleNavigationItemClick(
    context : Context , item : NavigationDrawerItem , drawerState : DrawerState , coroutineScope : CoroutineScope
) {
    when (item.title) {
        com.d4rk.android.libs.apptoolkit.R.string.settings -> {
            IntentsHelper.openActivity(
                context = context , activityClass = SettingsActivity::class.java
            )
        }

        com.d4rk.android.libs.apptoolkit.R.string.help_and_feedback -> {
            IntentsHelper.openActivity(
                context = context , activityClass = HelpActivity::class.java
            )
        }

        com.d4rk.android.libs.apptoolkit.R.string.updates -> {
            IntentsHelper.openUrl(
                context = context , url = "https://github.com/D4rK7355608/${context.packageName}/blob/master/CHANGELOG.md"
            )
        }

        com.d4rk.android.libs.apptoolkit.R.string.share -> {
            IntentsHelper.shareApp(
                context = context , shareMessageFormat = com.d4rk.android.libs.apptoolkit.R.string.summary_share_message
            )
        }
    }
    coroutineScope.launch { drawerState.close() }
}