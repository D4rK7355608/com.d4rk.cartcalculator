package com.d4rk.cartcalculator.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.RemoveCircleOutline
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.db.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.dialogs.NewCartItemDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartActivityComposable(activity : CartActivity , viewModel : CartViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val cartId = activity.intent.getIntExtra("cartId", 0)
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(viewModel.cartItems) {
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
                    text = "Your shopping cart is empty",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Box {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(viewModel.cartItems) { cartItem ->
                                CartItemComposable(
                                    cartItem = cartItem,
                                    onMinusClick = { viewModel.decreaseQuantity(cartItem) },
                                    onPlusClick = { viewModel.increaseQuantity(cartItem) },
                                    quantityState = viewModel.getQuantityStateForItem(cartItem)
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .height(144.dp)
                                    .clip(RoundedCornerShape(topStart = 16.dp , topEnd = 16.dp , bottomEnd = 0.dp , bottomStart = 0.dp)) ,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ) ,
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Total", style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }

            if (viewModel.openDialog.value) {
                NewCartItemDialog(cartId ,
                                  onDismiss = { viewModel.openDialog.value = false } ,
                                  onCartCreated = { cartItem ->
                                      cartItem.cartId = cartId
                                      viewModel.addCartItem(cartItem)
                                      viewModel.openDialog.value = false
                                  })

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
@Composable
fun CartItemComposable(
    cartItem: ShoppingCartItemsTable,
    onMinusClick: (ShoppingCartItemsTable) -> Unit,
    onPlusClick: (ShoppingCartItemsTable) -> Unit,
    quantityState: MutableState<Int>
) {
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
                Text(text = cartItem.price, style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onMinusClick(cartItem) }) {
                    Icon(
                        imageVector = Icons.Outlined.RemoveCircleOutline,
                        contentDescription = "Decrease Quantity"
                    )
                }
                Text(
                    text = quantityState.value.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                IconButton(onClick = { onPlusClick(cartItem) }) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircleOutline,
                        contentDescription = "Increase Quantity"
                    )
                }
            }
        }
    }
}