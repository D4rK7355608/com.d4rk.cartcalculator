package com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components.effects

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.main.ui.routes.home.ui.HomeViewModel
import com.d4rk.cartcalculator.core.domain.model.ui.UiStateScreen

@Composable
fun HandleHomeEvents(backStackEntry : NavBackStackEntry , homeViewModel : HomeViewModel , homeScreenState : UiStateScreen<UiHomeData> , snackbarHostState : SnackbarHostState) {
    val context = LocalContext.current
    val toggleImportDialog = backStackEntry.savedStateHandle.getStateFlow("toggleImportDialog" , false).collectAsState()
    val openNewCartDialog = backStackEntry.savedStateHandle.getStateFlow("openNewCartDialog" , false).collectAsState()

    LaunchedEffect(toggleImportDialog.value) {
        if (toggleImportDialog.value) {
            homeViewModel.sendEvent(HomeAction.ToggleImportDialog(true))
            backStackEntry.savedStateHandle["toggleImportDialog"] = false
        }
    }

    LaunchedEffect(openNewCartDialog.value) {
        if (openNewCartDialog.value) {
            homeViewModel.sendEvent(HomeAction.OpenNewCartDialog)
            backStackEntry.savedStateHandle["openNewCartDialog"] = false
        }
    }

    LaunchedEffect(homeScreenState.snackbar) {
        homeScreenState.snackbar?.let { snackbar ->
            backStackEntry.savedStateHandle["currentSnackbarIsError"] = snackbar.isError

            snackbarHostState.showSnackbar(
                message = snackbar.message.asString(context) , duration = if (snackbar.isError) SnackbarDuration.Long else SnackbarDuration.Short
            )

            homeViewModel.sendEvent(HomeAction.DismissSnackbar)
            backStackEntry.savedStateHandle.remove<Boolean>("currentSnackbarIsError")
        }
    }
}