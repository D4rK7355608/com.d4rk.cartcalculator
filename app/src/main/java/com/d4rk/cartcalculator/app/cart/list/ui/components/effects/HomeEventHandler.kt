package com.d4rk.cartcalculator.app.cart.list.ui.components.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavBackStackEntry
import com.d4rk.cartcalculator.app.cart.list.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.cart.list.ui.HomeViewModel

@Composable
fun HomeEventHandler(
    backStackEntry : NavBackStackEntry , homeViewModel : HomeViewModel
) {
    val toggleImportDialog : State<Boolean> = backStackEntry.savedStateHandle.getStateFlow(key = "toggleImportDialog" , initialValue = false).collectAsState()
    val openNewCartDialog : State<Boolean> = backStackEntry.savedStateHandle.getStateFlow(key = "openNewCartDialog" , initialValue = false).collectAsState()

    LaunchedEffect(key1 = toggleImportDialog.value) {
        if (toggleImportDialog.value) {
            homeViewModel.onEvent(event = HomeEvent.ToggleImportDialog(true))
            backStackEntry.savedStateHandle["toggleImportDialog"] = false
        }
    }

    LaunchedEffect(key1 = openNewCartDialog.value) {
        if (openNewCartDialog.value) {
            homeViewModel.onEvent(event = HomeEvent.OpenNewCartDialog)
            backStackEntry.savedStateHandle["openNewCartDialog"] = false
        }
    }
}