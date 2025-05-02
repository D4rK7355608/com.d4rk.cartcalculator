package com.d4rk.cartcalculator.app.home.ui

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.animations.rememberAnimatedVisibilityState
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.animateVisibility
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHandler
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.home.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.home.domain.model.HomeListItem
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.ui.components.CartItem
import com.d4rk.cartcalculator.app.home.ui.components.HomeScreenSortFilterRow
import com.d4rk.cartcalculator.app.home.ui.components.effects.HomeScreenDialogs
import com.d4rk.cartcalculator.app.home.ui.utils.constants.UiConstants
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.utils.helpers.ShareHelper
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

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

    DefaultSnackbarHandler(screenState = screenState , snackbarHostState = snackbarHostState , getDismissEvent = { HomeEvent.DismissSnackbar } , onEvent = { viewModel.onEvent(it) })
    HomeScreenDialogs(screenState = screenState , viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(paddingValues : PaddingValues = PaddingValues() , uiState : UiHomeData , viewModel : HomeViewModel , onFabVisibilityChanged : (Boolean) -> Unit , adsConfig : AdsConfig = koinInject(qualifier = named(name = "banner_medium_rectangle"))) {

    val combinedList : List<HomeListItem> = buildList {
        uiState.carts.forEachIndexed { index : Int , cart : ShoppingCartTable ->
            add(element = HomeListItem.CartItem(cart = cart))
            if ((index + 1) % UiConstants.AD_INTERVAL == 0) add(element = HomeListItem.AdItem)
        }

        if (uiState.carts.isNotEmpty() && uiState.carts.size % UiConstants.AD_INTERVAL != 0) {
            add(element = HomeListItem.AdItem)
        }
    }

    val listState : LazyListState = rememberLazyListState()
    val (visibilityStates : SnapshotStateList<Boolean> , isFabVisible : MutableState<Boolean>) = rememberAnimatedVisibilityState(listState = listState , itemCount = uiState.carts.size)

    val context : Context = LocalContext.current

    LaunchedEffect(key1 = uiState.shareLink) {
        uiState.shareLink?.let { link : String ->
            ShareHelper.shareText(context = context , link = link)
            viewModel.updateUi { copy(shareLink = null) }
        }
    }

    LaunchedEffect(key1 = isFabVisible.value) {
        onFabVisibilityChanged(isFabVisible.value)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = listState , contentPadding = paddingValues , modifier = Modifier.fillMaxSize() , verticalArrangement = Arrangement.spacedBy(space = SizeConstants.MediumSize)) {
            if (uiState.carts.size > 10) {
                stickyHeader {
                    HomeScreenSortFilterRow(viewModel = viewModel)
                }
            }

            itemsIndexed(items = combinedList , key = { index : Int , item : HomeListItem ->
                when (item) {
                    is HomeListItem.CartItem -> "cart_${item.cart.cartId}"
                    is HomeListItem.AdItem -> "ad_$index"
                }
            }) { index : Int , item : HomeListItem ->
                when (item) {
                    is HomeListItem.CartItem -> {
                        CartItem(
                            cart = item.cart ,
                                 onDelete = { viewModel.onEvent(event = HomeEvent.OpenDeleteCartDialog(cart = item.cart)) } ,
                                 onCardClick = { viewModel.onEvent(event = HomeEvent.OpenCart(cart = item.cart)) } ,
                                 onShare = { viewModel.onEvent(event = HomeEvent.GenerateCartShareLink(cart = it)) } ,
                                 onRename = { viewModel.onEvent(event = HomeEvent.OpenRenameCartDialog(cart = it)) } ,
                                 uiState = uiState ,
                                 modifier = Modifier
                                         .animateItem()
                                         .animateVisibility(visible = visibilityStates.getOrElse(index = index) { false } , index = index))
                    }

                    is HomeListItem.AdItem -> {
                        AdBanner(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = SizeConstants.MediumSize) , adsConfig = adsConfig
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(height = UiConstants.BOTTOM_SPACER_HEIGHT.dp))
            }
        }
    }
}