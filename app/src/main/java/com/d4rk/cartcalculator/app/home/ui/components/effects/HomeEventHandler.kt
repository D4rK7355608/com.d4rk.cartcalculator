package com.d4rk.cartcalculator.app.home.ui.components.effects

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel

@Composable
fun HomeEventHandler(backStackEntry : NavBackStackEntry , homeViewModel : HomeViewModel , homeScreenState : UiStateScreen<UiHomeData> , snackbarHostState : SnackbarHostState) {
    val context : Context = LocalContext.current
    val toggleImportDialog : State<Boolean> = backStackEntry.savedStateHandle.getStateFlow(key = "toggleImportDialog" , initialValue = false).collectAsState()
    val openNewCartDialog : State<Boolean> = backStackEntry.savedStateHandle.getStateFlow(key = "openNewCartDialog" , initialValue = false).collectAsState()

    LaunchedEffect(key1 = toggleImportDialog.value) {
        if (toggleImportDialog.value) {
            homeViewModel.sendEvent(event = HomeAction.ToggleImportDialog(true))
            backStackEntry.savedStateHandle["toggleImportDialog"] = false
        }
    }

    LaunchedEffect(key1 = openNewCartDialog.value) {
        if (openNewCartDialog.value) {
            homeViewModel.sendEvent(event = HomeAction.OpenNewCartDialog)
            backStackEntry.savedStateHandle["openNewCartDialog"] = false
        }
    }

    LaunchedEffect(key1 = homeScreenState.snackbar) {
        homeScreenState.snackbar?.let { snackbar ->
            backStackEntry.savedStateHandle["currentSnackbarIsError"] = snackbar.isError

            snackbarHostState.showSnackbar(
                message = snackbar.message.asString(context) , duration = if (snackbar.isError) SnackbarDuration.Long else SnackbarDuration.Short
            )

            homeViewModel.sendEvent(event = HomeAction.DismissSnackbar)
            backStackEntry.savedStateHandle.remove<Boolean>("currentSnackbarIsError")
        }
    }
}