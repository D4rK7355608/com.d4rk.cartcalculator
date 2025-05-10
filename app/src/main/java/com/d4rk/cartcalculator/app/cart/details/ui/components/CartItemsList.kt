package com.d4rk.cartcalculator.app.cart.details.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.animations.rememberAnimatedVisibilityState
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.animateVisibility
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.app.cart.details.domain.actions.CartEvent
import com.d4rk.cartcalculator.app.cart.details.domain.model.ui.CartListItem
import com.d4rk.cartcalculator.app.cart.details.domain.model.ui.UiCartScreen
import com.d4rk.cartcalculator.app.cart.details.ui.CartViewModel
import com.d4rk.cartcalculator.app.cart.details.ui.utils.helpers.CartListBuilderHelper
import com.d4rk.cartcalculator.app.cart.list.ui.utils.constants.ui.UiConstants
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun CartItemsList(modifier : Modifier , viewModel : CartViewModel , adsConfig : AdsConfig = koinInject(named("banner_medium_rectangle"))) {
    val uiState : UiStateScreen<UiCartScreen> by viewModel.screenState.collectAsState()
    val adsEnabled : Boolean by remember { viewModel.dataStore.ads(default = true) }.collectAsState(initial = true)
    val cartItems : List<ShoppingCartItemsTable> = uiState.data?.cartItems ?: emptyList()
    val showTotalCard : Boolean = uiState.data?.totalPrice?.let { it > 0 } == true && uiState.data?.cartItems?.isNotEmpty() == true
    val listState : LazyListState = rememberLazyListState()

    val combinedList : List<CartListItem> = remember<List<CartListItem>>(key1 = cartItems , key2 = adsEnabled) {
        CartListBuilderHelper.buildUnifiedCartList(items = cartItems , adsEnabled = adsEnabled)
    }

    val (visibilityStates : SnapshotStateList<Boolean>) = rememberAnimatedVisibilityState(listState = listState , itemCount = combinedList.count { it is CartListItem.CartItem })

    LazyColumn(modifier = modifier , state = listState) {
        itemsIndexed(items = combinedList , key = { index : Int , item : CartListItem ->
            when (item) {
                is CartListItem.Header -> item.label
                is CartListItem.CartItem -> "cart_${item.item.itemId}"
                is CartListItem.AdItem -> "ad_$index"
            }
        }) { index : Int , item : CartListItem ->
            when (item) {
                is CartListItem.Header -> {
                    Text(
                        text = item.label , style = MaterialTheme.typography.titleMedium , modifier = Modifier
                                .animateItem()
                                .padding(start = SizeConstants.LargeSize , top = SizeConstants.SmallSize)
                    )
                }

                is CartListItem.CartItem -> {
                    val visibleIndex : Int = combinedList.take(n = index).count { it is CartListItem.CartItem }
                    val isVisible : Boolean = visibilityStates.getOrElse<Boolean>(index = visibleIndex) { false }

                    CartItem(
                        viewModel = viewModel ,
                             cartItem = item.item ,
                             onMinusClick = { viewModel.onEvent(event = CartEvent.DecreaseQuantity(item = it)) } ,
                             onPlusClick = { viewModel.onEvent(event = CartEvent.IncreaseQuantity(item = it)) } ,
                             uiState = uiState ,
                             modifier = Modifier
                                     .animateItem()
                                     .animateVisibility(visible = isVisible , index = visibleIndex))
                }

                is CartListItem.AdItem -> {
                    AdBanner(
                        modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .padding(bottom = SizeConstants.MediumSize) , adsConfig = adsConfig
                    )
                }
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(
                    height = if (showTotalCard) UiConstants.BOTTOM_SPACER_WITH_TOTAL.dp
                    else UiConstants.BOTTOM_SPACER_HEIGHT.dp
                )
            )
        }
    }
}