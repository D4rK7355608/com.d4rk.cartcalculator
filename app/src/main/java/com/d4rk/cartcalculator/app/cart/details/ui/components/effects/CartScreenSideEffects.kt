package com.d4rk.cartcalculator.app.cart.details.ui.components.effects

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.d4rk.cartcalculator.app.cart.details.ui.CartViewModel
import com.d4rk.cartcalculator.core.utils.helpers.ShareHelper

@Composable
fun CartScreenSideEffects(
    shareLink: String?,
    context: Context,
    viewModel: CartViewModel,
) {
    LaunchedEffect(key1 = shareLink) {
        shareLink?.let {
            ShareHelper.shareText(context = context, link = it)
            viewModel.updateUi { copy(shareCartLink = null) }
        }
    }
}
