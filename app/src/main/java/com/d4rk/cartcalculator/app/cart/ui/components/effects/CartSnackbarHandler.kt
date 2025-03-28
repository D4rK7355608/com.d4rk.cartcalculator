package com.d4rk.cartcalculator.app.cart.ui.components.effects

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.cart.ui.CartViewModel

@Composable
fun CartSnackbarHandler(screenState : UiStateScreen<UiCartScreen> , viewModel : CartViewModel , snackbarHostState : SnackbarHostState) {
    val context = LocalContext.current

    LaunchedEffect(key1 = screenState.snackbar) {
        screenState.snackbar?.let { snackbar ->
            val result = snackbarHostState.showSnackbar(
                message = snackbar.message.asString(context) , duration = if (snackbar.isError) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                viewModel.sendEvent(CartAction.DismissSnackbar)
            }
        }
    }
}