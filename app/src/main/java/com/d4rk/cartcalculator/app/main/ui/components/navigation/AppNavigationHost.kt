package com.d4rk.cartcalculator.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.d4rk.android.libs.apptoolkit.app.help.ui.HelpActivity
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.NavigationHost
import com.d4rk.android.libs.apptoolkit.app.settings.settings.ui.SettingsActivity
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.utils.constants.links.AppLinks
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cartcalculator.app.cart.list.domain.model.ui.UiHomeData
import com.d4rk.cartcalculator.app.cart.list.ui.HomeScreen
import com.d4rk.cartcalculator.app.cart.list.ui.HomeViewModel
import com.d4rk.cartcalculator.app.cart.list.ui.components.effects.HomeEventHandler
import com.d4rk.cartcalculator.app.main.utils.constants.NavigationRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavigationHost(
    navController : NavHostController ,
    snackBarHostState : SnackbarHostState ,
    onFabVisibilityChanged : (Boolean) -> Unit ,
    paddingValues : PaddingValues ,
    homeViewModel : HomeViewModel ,
    homeScreenState : UiStateScreen<UiHomeData> ,
    currentSearchQuery : String ,
    onSearchQueryChange : (String) -> Unit
) {
    NavigationHost(navController = navController , startDestination = NavigationRoutes.ROUTE_EVENTS_LIST) {
        composable(route = NavigationRoutes.ROUTE_EVENTS_LIST) { backStackEntry ->
            HomeScreen(paddingValues = paddingValues , viewModel = homeViewModel , onFabVisibilityChanged = onFabVisibilityChanged , snackBarHostState = snackBarHostState , screenState = homeScreenState)
        }
        composable(
            route = NavigationRoutes.ROUTE_SEARCH , arguments = listOf(navArgument(NavigationRoutes.ARG_INITIAL_QUERY) {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val initialQueryArg = backStackEntry.arguments?.getString(NavigationRoutes.ARG_INITIAL_QUERY)
            SearchScreen(initialQueryEncoded = initialQueryArg , paddingValues = paddingValues)
        }
    }
}

fun handleNavigationItemClick(context : Context , item : NavigationDrawerItem , drawerState : DrawerState? = null , coroutineScope : CoroutineScope? = null) {
    when (item.title) {
        com.d4rk.android.libs.apptoolkit.R.string.settings -> IntentsHelper.openActivity(context = context , activityClass = SettingsActivity::class.java)
        com.d4rk.android.libs.apptoolkit.R.string.help_and_feedback -> IntentsHelper.openActivity(context = context , activityClass = HelpActivity::class.java)
        com.d4rk.android.libs.apptoolkit.R.string.updates -> IntentsHelper.openUrl(context = context , url = AppLinks.githubChangelog(context.packageName))
        com.d4rk.android.libs.apptoolkit.R.string.share -> IntentsHelper.shareApp(context = context , shareMessageFormat = com.d4rk.android.libs.apptoolkit.R.string.summary_share_message)
    }
    drawerState?.let { drawerState : DrawerState ->
        coroutineScope?.let { scope : CoroutineScope ->
            scope.launch { drawerState.close() }
        }
    }
}