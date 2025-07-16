package com.d4rk.cartcalculator.app.cart.list.ui.components.effects

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.lifecycle.Lifecycle
import com.d4rk.android.libs.apptoolkit.core.ui.effects.ActivityLifecycleEffect
import com.d4rk.cartcalculator.app.cart.list.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.cart.list.ui.HomeViewModel
import com.d4rk.cartcalculator.app.cart.list.ui.utils.constants.ui.UiConstants
import com.d4rk.cartcalculator.core.utils.helpers.ShareHelper

@Composable
fun HomeScreenSideEffects(
    currentCount: Int,
    previousCartCount: MutableIntState,
    shareLink: String?,
    listState: LazyListState,
    combinedListSize: Int,
    context: Context,
    onFabVisibilityChanged: (Boolean) -> Unit,
    isFabVisible: Boolean,
    viewModel: HomeViewModel,
) {
    // 🎯 Auto scroll on new item
    LaunchedEffect(key1 = currentCount) {
        if (currentCount > previousCartCount.intValue) {
            val targetIndex: Int =
                if (currentCount == UiConstants.STICKY_HEADER_THRESHOLD) 0 else combinedListSize - 1
            listState.animateScrollToItem(index = targetIndex)
        }
        previousCartCount.intValue = currentCount
    }

    // 🔗 Handle share intent
    LaunchedEffect(key1 = shareLink) {
        shareLink?.let {
            ShareHelper.shareText(context = context, link = it)
            viewModel.updateUi { copy(shareLink = null) }
        }
    }

    // 📱 FAB visibility
    LaunchedEffect(key1 = isFabVisible) {
        onFabVisibilityChanged(isFabVisible)
    }

    ActivityLifecycleEffect(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.onEvent(event = HomeEvent.LoadCarts)
    }
}