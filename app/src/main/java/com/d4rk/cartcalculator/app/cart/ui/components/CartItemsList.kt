package com.d4rk.cartcalculator.app.cart.ui.components

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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.ui.components.animations.rememberAnimatedVisibilityState
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.animateVisibility
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.cart.ui.CartViewModel
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

@Composable
fun CartItemsList(modifier : Modifier , viewModel : CartViewModel) {
    val uiState : UiStateScreen<UiCartScreen> by viewModel.screenState.collectAsState()
    val listState : LazyListState = rememberLazyListState()
    val (checkedItems : List<ShoppingCartItemsTable> , uncheckedItems : List<ShoppingCartItemsTable>) = uiState.data?.cartItems?.partition { it.isChecked } ?: Pair(emptyList() , emptyList())
    val (visibilityStates : SnapshotStateList<Boolean>) = rememberAnimatedVisibilityState(listState = listState , itemCount = uiState.data?.cartItems?.size ?: 0)

    LazyColumn(modifier = modifier , state = listState ,) {
        if (checkedItems.isNotEmpty()) {
            item(key = "checked_items") {
                Text(
                    text = stringResource(id = R.string.in_cart) , style = MaterialTheme.typography.titleMedium , modifier = Modifier
                            .animateItem()
                            .padding(start = SizeConstants.LargeSize , top = SizeConstants.SmallSize)
                )
            }
            itemsIndexed(items = checkedItems , key = { _ , item -> item.itemId }) { index , cartItem ->
                val isVisible : Boolean = visibilityStates.getOrElse(index = index) { false }
                CartItem(
                    viewModel = viewModel ,
                         cartItem = cartItem ,
                         onMinusClick = { item -> viewModel.sendEvent(CartAction.DecreaseQuantity(item)) } ,
                         onPlusClick = { item -> viewModel.sendEvent(CartAction.IncreaseQuantity(item)) } ,
                         uiState = uiState ,
                         modifier = Modifier
                                 .animateItem()
                                 .animateVisibility(visible = isVisible , index = index))
            }
        }

        if (uncheckedItems.isNotEmpty()) {
            item(key = "unchecked_items") {
                Text(
                    text = stringResource(id = R.string.items_to_pick_up) , style = MaterialTheme.typography.titleMedium , modifier = Modifier
                            .animateItem()
                            .padding(start = SizeConstants.LargeSize , top = SizeConstants.SmallSize)
                )
            }
            itemsIndexed(items = uncheckedItems , key = { _ , item -> item.itemId }) { index , cartItem ->
                val isVisible : Boolean = visibilityStates.getOrElse(index = index) { false }
                CartItem(
                    viewModel = viewModel ,
                         cartItem = cartItem ,
                         onMinusClick = { item -> viewModel.sendEvent(CartAction.DecreaseQuantity(item)) } ,
                         onPlusClick = { item -> viewModel.sendEvent(CartAction.IncreaseQuantity(item)) } ,
                         uiState = uiState ,
                         modifier = Modifier
                                 .animateItem()
                                 .animateVisibility(visible = isVisible , index = index))
            }
        }
    }
}