package com.d4rk.cartcalculator.app.home.ui.components.effects

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.d4rk.cartcalculator.app.home.domain.events.HomeUiEvent
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel
import com.d4rk.cartcalculator.core.utils.helpers.ShareHelper

@Composable
fun HomeSnackbarHandler(
    viewModel : HomeViewModel , snackbarHostState : SnackbarHostState
) {
    val context : Context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context) , duration = if (event.isError) SnackbarDuration.Long else SnackbarDuration.Short
                    )
                }

                is HomeUiEvent.ShareCart -> {
                    ShareHelper.shareText(context = context , link = event.link)
                }
            }
        }
    }
}