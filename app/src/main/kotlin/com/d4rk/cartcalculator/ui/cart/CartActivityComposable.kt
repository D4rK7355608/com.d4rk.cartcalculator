package com.d4rk.cartcalculator.ui.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.ads.BannerAdsComposable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.ui.dialogs.DeleteCartItemDialog
import com.d4rk.cartcalculator.ui.dialogs.NewCartItemDialog
import com.d4rk.cartcalculator.utils.bounceClick
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartActivityComposable(activity: CartActivity, viewModel: CartViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val cartId = activity.intent.getIntExtra("cartId", 0)
    val primaryColor = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)

    LaunchedEffect(viewModel.cartItems.size) {
        if (viewModel.cartItems.isEmpty()) {
            activity.window.navigationBarColor = Color.Transparent.toArgb()
        } else {
            activity.window.navigationBarColor = primaryColor.toArgb()
        }
    }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = {
            Text(
                viewModel.cart.value?.name ?: stringResource(R.string.shopping_cart)
            )
        }, navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }, actions = {
            IconButton(onClick = {
                viewModel.openDialog.value = true
            }) {
                Icon(
                    Icons.Outlined.AddShoppingCart, contentDescription = null,
                )
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (viewModel.isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.cartItems.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.your_shopping_cart_is_empty),
                    modifier = Modifier.align(Alignment.Center)
                )
                BannerAdsComposable(
                    modifier = Modifier.align(Alignment.BottomCenter), dataStore = dataStore
                )
            } else {
                Box {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(
                                items = viewModel.cartItems,
                                key = { item -> item.itemId }) { cartItem ->
                                CartItemComposable(
                                    viewModel = viewModel,
                                    cartItem = cartItem,
                                    onMinusClick = { viewModel.decreaseQuantity(cartItem) },
                                    onPlusClick = { viewModel.increaseQuantity(cartItem) },
                                )
                            }
                        }

                        BannerAdsComposable(
                            modifier = Modifier.padding(12.dp), dataStore = dataStore
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(144.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomEnd = 0.dp,
                                        bottomStart = 0.dp
                                    )
                                ),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.total),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        Text(
                                            text = String.format(
                                                Locale.US,
                                                "%.1f",
                                                viewModel.totalPrice.doubleValue.toFloat()
                                            ).removeSuffix(".0"),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = viewModel.selectedCurrency.value,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (viewModel.openDialog.value) {
                NewCartItemDialog(
                    cartId = cartId,
                    onDismiss = { viewModel.openDialog.value = false },
                    onCartCreated = { cartItem ->
                        cartItem.cartId = cartId
                        viewModel.addCartItem(cartItem)
                        viewModel.openDialog.value = false
                    })

            }

            if (viewModel.openDeleteDialog.value) {
                val currentItem = viewModel.currentCartItemForDeletion
                if (currentItem != null) {
                    DeleteCartItemDialog(
                        cartItem = currentItem,
                        onDismiss = { viewModel.openDeleteDialog.value = false },
                        onDeleteConfirmed = {
                            viewModel.deleteCartItem(currentItem)
                            viewModel.openDeleteDialog.value = false
                            viewModel.currentCartItemForDeletion = null
                        }
                    )
                }
            }
        }
    }
}

/**
 * This Composable function displays a cart item with the ability to modify its quantity.
 * It shows the item's name, price, and a numeric representation of quantity, which can be adjusted via plus and minus buttons.
 *
 * @param cartItem The ShoppingCartItemsTable object representing the cart item.
 * @param onMinusClick Lambda function invoked when the minus button is clicked, decreasing the quantity.
 * @param onPlusClick Lambda function invoked when the plus button is clicked, increasing the quantity.
 * @param quantityState A MutableState that holds and updates the quantity of the cart item.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartItemComposable(
    viewModel: CartViewModel,
    cartItem: ShoppingCartItemsTable,
    onMinusClick: (ShoppingCartItemsTable) -> Unit,
    onPlusClick: (ShoppingCartItemsTable) -> Unit,
) {
    val quantityState = viewModel.getQuantityStateForItem(cartItem)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = cartItem.name, style = MaterialTheme.typography.bodyLarge)
                Row {
                    Text(
                        text = String.format(Locale.US, "%.1f", cartItem.price.toFloat())
                            .removeSuffix(".0"), style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = viewModel.selectedCurrency.value,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .bounceClick()
                        .size(40.dp)
                        .clip(CircleShape)
                        .combinedClickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = true),
                            onClick = {
                                onMinusClick(cartItem)
                            },
                            onLongClick = {
                                viewModel.currentCartItemForDeletion = cartItem
                                viewModel.openDeleteDialog.value = true
                            },
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RemoveCircleOutline,
                        contentDescription = stringResource(id = R.string.decrease_quantity),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    text = quantityState.value.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                IconButton(modifier = Modifier.bounceClick(),
                    onClick = { onPlusClick(cartItem) }) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircleOutline,
                        contentDescription = stringResource(id = R.string.increase_quantity)
                    )
                }
            }
        }
    }
}