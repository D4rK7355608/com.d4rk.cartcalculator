package com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components.effects

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.main.ui.routes.home.ui.HomeViewModel
import com.d4rk.cartcalculator.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.core.utils.helpers.ShareHelper

@Composable
fun HomeScreenSnackbar(
    screenState: UiStateScreen<UiHomeData>,
    viewModel: HomeViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    LaunchedEffect(screenState.snackbar) {
        screenState.snackbar?.let { snackbar ->
            val result = snackbarHostState.showSnackbar(
                message = snackbar.message.asString(context),
                duration = if (snackbar.isError) SnackbarDuration.Long else SnackbarDuration.Short
            )

            if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                viewModel.sendEvent(HomeAction.DismissSnackbar)
            }
        }
    }

    LaunchedEffect(screenState.data?.shareCartLink) {
        screenState.data?.shareCartLink?.let { link ->
            ShareHelper.shareText(context, link)
            viewModel.sendEvent(HomeAction.DismissSnackbar)
        }
    }
}