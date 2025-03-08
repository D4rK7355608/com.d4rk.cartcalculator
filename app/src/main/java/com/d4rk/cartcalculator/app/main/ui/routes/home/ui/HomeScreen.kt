package com.d4rk.cartcalculator.app.main.ui.routes.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.animateVisibility
import com.d4rk.android.libs.apptoolkit.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components.CartItem
import com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components.effects.HomeScreenSnackbar
import com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components.dialogs.HomeDialogs
import com.d4rk.cartcalculator.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.core.ui.animations.rememberAnimatedVisibilityState
import com.d4rk.cartcalculator.core.ui.layouts.LoadingScreen
import com.d4rk.cartcalculator.core.ui.layouts.NoDataScreen
import com.d4rk.cartcalculator.core.ui.layouts.ScreenStateHandler

@Composable
fun HomeScreen(paddingValues : PaddingValues , viewModel : HomeViewModel , onFabVisibilityChanged : (Boolean) -> Unit , snackbarHostState : SnackbarHostState, screenState : UiStateScreen<UiHomeData>) {
    ScreenStateHandler(screenState = screenState , onLoading = {
        onFabVisibilityChanged(false)
        LoadingScreen()
    } , onEmpty = {
        onFabVisibilityChanged(true)
        NoDataScreen(text = R.string.no_carts_available , icon = Icons.Outlined.RemoveShoppingCart)
    } , onSuccess = { uiState ->
        HomeScreenContent(paddingValues = paddingValues , uiState = uiState , viewModel = viewModel , onFabVisibilityChanged = onFabVisibilityChanged)
    })

    HomeScreenSnackbar(screenState = screenState , viewModel = viewModel , snackbarHostState = snackbarHostState)

    HomeDialogs(screenState = screenState , viewModel = viewModel)
}

@Composable
fun HomeScreenContent(
    paddingValues : PaddingValues = PaddingValues() , uiState : UiHomeData , viewModel : HomeViewModel , onFabVisibilityChanged : (Boolean) -> Unit
) {
    val listState : LazyListState = rememberLazyListState()

    val (visibilityStates : SnapshotStateList<Boolean> , isFabVisible : MutableState<Boolean>) = rememberAnimatedVisibilityState(listState = listState , itemCount = uiState.carts.size)

    LaunchedEffect(key1 = isFabVisible.value) {
        onFabVisibilityChanged(isFabVisible.value)
    }

    LazyColumn(
        state = listState , contentPadding = paddingValues , modifier = Modifier.fillMaxSize() , verticalArrangement = Arrangement.spacedBy(space = SizeConstants.MediumSize)
    ) {
        itemsIndexed(items = uiState.carts , key = { _ , item -> item.cartId }) { index , cart ->
            CartItem(cart = cart ,
                     onDelete = { viewModel.sendEvent(HomeAction.OpenDeleteCartDialog(cart)) } ,
                     onCardClick = { viewModel.sendEvent(HomeAction.OpenCart(cart)) } ,
                     uiState = uiState ,
                     modifier = Modifier
                             .animateItem()
                             .animateVisibility(visible = visibilityStates.getOrElse(index) { false } , index = index) ,
                     onShare = { sharedCart ->
                         viewModel.sendEvent(HomeAction.GenerateCartShareLink(sharedCart))
                     })
        }
    }
}