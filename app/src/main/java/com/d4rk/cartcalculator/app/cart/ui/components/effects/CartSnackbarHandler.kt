package com.d4rk.cartcalculator.app.cart.ui.components.effects

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.cart.domain.actions.CartEvent
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.cart.ui.CartViewModel
import com.d4rk.cartcalculator.core.utils.helpers.ShareHelper

@Composable
fun CartSnackbarHandler(screenState : UiStateScreen<UiCartScreen> , viewModel : CartViewModel , snackbarHostState : SnackbarHostState) {
    val context : Context = LocalContext.current

    LaunchedEffect(key1 = screenState.snackbar) {
        screenState.snackbar?.let { snackbar : UiSnackbar ->
            val result : SnackbarResult = snackbarHostState.showSnackbar(message = snackbar.message.asString(context = context) , duration = if (snackbar.isError) SnackbarDuration.Long else SnackbarDuration.Short)
            if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                viewModel.onEvent(event = CartEvent.DismissSnackbar)
            }
        }
    }

    // 🔗 Handle share cart link
    LaunchedEffect(key1 = screenState.data?.shareCartLink) {
        screenState.data?.shareCartLink?.let { sharedText : String ->
            ShareHelper.shareText(context = context , link = sharedText)
            viewModel.updateUi { copy(shareCartLink = null) }
        }
    }
}