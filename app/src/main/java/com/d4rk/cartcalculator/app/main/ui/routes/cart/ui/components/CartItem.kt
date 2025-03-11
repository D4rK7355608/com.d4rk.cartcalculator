package com.d4rk.cartcalculator.app.main.ui.routes.cart.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.ExtraSmallHorizontalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.main.ui.routes.cart.ui.CartViewModel
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartItem(viewModel : CartViewModel , cartItem : ShoppingCartItemsTable , onMinusClick : (ShoppingCartItemsTable) -> Unit , onPlusClick : (ShoppingCartItemsTable) -> Unit , uiState : UiCartScreen , modifier : Modifier) {
    val view : View = LocalView.current
    var checkedState by remember { mutableStateOf(cartItem.isChecked) }
    val quantityState = viewModel.getQuantityStateForItem(item = cartItem)
    val interactionSource = remember { MutableInteractionSource() }
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = { target ->
        when (target) {
            SwipeToDismissBoxValue.StartToEnd -> {
                viewModel.sendEvent(event = CartAction.OpenEditDialog(item = cartItem))
                false
            }

            SwipeToDismissBoxValue.EndToStart -> {
                viewModel.sendEvent(event = CartAction.OpenDeleteDialog(item = cartItem))
                false
            }

            else -> true
        }
    })

    LaunchedEffect(dismissState.targetValue , dismissState.currentValue) {
        if (dismissState.currentValue == dismissState.targetValue) {
            dismissState.reset()
        }
        else if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
            if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                viewModel.sendEvent(event = CartAction.OpenEditDialog(item = cartItem))
            }
            else if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                viewModel.sendEvent(event = CartAction.OpenDeleteDialog(item = cartItem))
            }
        }
    }

    SwipeToDismissBox(state = dismissState , backgroundContent = { /* Optional background */ } , content = {
        Box(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(all = 24.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize() , horizontalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.weight(weight = 1f)) {
                    Checkbox(modifier = Modifier
                            .bounceClick()
                            .padding(end = SizeConstants.LargeSize)
                            .wrapContentSize() , checked = checkedState , onCheckedChange = { isChecked ->
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        checkedState = isChecked
                        viewModel.sendEvent(event = CartAction.ItemCheckedChange(item = cartItem , isChecked = isChecked))
                    })
                    Column(modifier = Modifier.weight(weight = 1f)) {
                        Text(text = cartItem.name , style = MaterialTheme.typography.bodyLarge , modifier = Modifier.basicMarquee())
                        Row {
                            Text(text = String.format(Locale.getDefault() , "%.1f" , cartItem.price.toFloat()).removeSuffix(suffix = ".0"))
                            ExtraSmallHorizontalSpacer()
                            Text(text = uiState.selectedCurrency , style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                Row(modifier = Modifier.wrapContentSize() , verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                                .bounceClick()
                                .size(size = 40.dp)
                                .clip(shape = CircleShape)
                                .combinedClickable(interactionSource = interactionSource , indication = remember { ripple() } , onClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    onMinusClick(cartItem)
                                } , onLongClick = {
                                    viewModel.sendEvent(event = CartAction.OpenDeleteDialog(item = cartItem))
                                })) {
                        Icon(imageVector = Icons.Outlined.RemoveCircleOutline , contentDescription = stringResource(id = R.string.decrease_quantity) , modifier = Modifier.align(Alignment.Center))
                    }
                    Text(
                        text = quantityState.toString() , style = MaterialTheme.typography.bodyMedium , modifier = Modifier
                                .padding(horizontal = SizeConstants.LargeSize)
                                .animateContentSize()
                    )
                    IconButton(
                        modifier = Modifier.bounceClick() , onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            onPlusClick(cartItem)
                        }) {
                        Icon(imageVector = Icons.Outlined.AddCircleOutline , contentDescription = stringResource(id = R.string.increase_quantity))
                    }
                }
            }
        }
    })
}