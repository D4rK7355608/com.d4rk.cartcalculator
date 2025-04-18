package com.d4rk.cartcalculator.app.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.animations.rememberAnimatedVisibilityState
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.animateVisibility
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.home.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.ui.components.CartItem
import com.d4rk.cartcalculator.app.home.ui.components.HomeScreenSortFilterRow
import com.d4rk.cartcalculator.app.home.ui.components.effects.HomeScreenDialogs
import com.d4rk.cartcalculator.app.home.ui.components.effects.HomeSnackbarHandler
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

@Composable
fun HomeScreen(paddingValues : PaddingValues , viewModel : HomeViewModel , onFabVisibilityChanged : (Boolean) -> Unit , snackbarHostState : SnackbarHostState , screenState : UiStateScreen<UiHomeData>) {
    ScreenStateHandler(screenState = screenState , onLoading = {
        onFabVisibilityChanged(false)
        LoadingScreen()
    } , onEmpty = {
        onFabVisibilityChanged(true)
        NoDataScreen(text = R.string.no_carts_available , icon = Icons.Outlined.RemoveShoppingCart)
    } , onSuccess = { uiState : UiHomeData ->
        HomeScreenContent(paddingValues = paddingValues , uiState = uiState , viewModel = viewModel , onFabVisibilityChanged = onFabVisibilityChanged)
    })

    HomeSnackbarHandler(viewModel = viewModel , snackbarHostState = snackbarHostState)
    HomeScreenDialogs(screenState = screenState , viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(paddingValues : PaddingValues = PaddingValues() , uiState : UiHomeData , viewModel : HomeViewModel , onFabVisibilityChanged : (Boolean) -> Unit) {
    val listState : LazyListState = rememberLazyListState()
    val (visibilityStates : SnapshotStateList<Boolean> , isFabVisible : MutableState<Boolean>) = rememberAnimatedVisibilityState(listState = listState , itemCount = uiState.carts.size)
    LaunchedEffect(key1 = isFabVisible.value) {
        onFabVisibilityChanged(isFabVisible.value)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = listState , contentPadding = paddingValues , modifier = Modifier.fillMaxSize() , verticalArrangement = Arrangement.spacedBy(space = SizeConstants.MediumSize)) {
            stickyHeader {
                if (uiState.carts.size > 10) {
                    HomeScreenSortFilterRow(viewModel = viewModel)
                }
            }
            itemsIndexed(items = uiState.carts , key = { _ , item -> item.cartId }) { index : Int , cart : ShoppingCartTable ->
                CartItem(
                    cart = cart ,
                    onDelete = { viewModel.onEvent(event = HomeEvent.OpenDeleteCartDialog(cart = cart)) } ,
                    onCardClick = { viewModel.onEvent(event = HomeEvent.OpenCart(cart = cart)) } ,
                    onShare = { sharedCart -> viewModel.onEvent(event = HomeEvent.GenerateCartShareLink(cart = sharedCart)) } ,
                    onRename = { renameCart -> viewModel.onEvent(event = HomeEvent.OpenRenameCartDialog(cart = renameCart)) } ,
                    uiState = uiState ,
                    modifier = Modifier
                                 .animateItem()
                                 .animateVisibility(visible = visibilityStates.getOrElse(index) { false } , index = index))
            }
            item {
                Spacer(modifier = Modifier.height(height = 72.dp))
            }
        }
    }
}