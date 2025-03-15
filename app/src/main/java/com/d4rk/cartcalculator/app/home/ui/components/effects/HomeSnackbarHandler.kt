package com.d4rk.cartcalculator.app.home.ui.components.effects

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel
import com.d4rk.cartcalculator.core.utils.helpers.ShareHelper

@Composable
fun HomeSnackbarHandler(screenState : UiStateScreen<UiHomeData> , viewModel : HomeViewModel , snackbarHostState : SnackbarHostState) {
    val context : Context = LocalContext.current

    LaunchedEffect(key1 = screenState.snackbar) {
        screenState.snackbar?.let { snackbar ->
            val result = snackbarHostState.showSnackbar(
                message = snackbar.message.asString(context) , duration = if (snackbar.isError) SnackbarDuration.Long else SnackbarDuration.Short
            )

            if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                viewModel.sendEvent(event = HomeAction.DismissSnackbar)
            }
        }
    }

    LaunchedEffect(key1 = screenState.data?.shareCartLink) {
        screenState.data?.shareCartLink?.let { link ->
            ShareHelper.shareText(context , link)
            viewModel.sendEvent(event = HomeAction.DismissSnackbar)
        }
    }
}