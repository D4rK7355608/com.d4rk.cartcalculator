package com.d4rk.cartcalculator.ui.home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.ads.BannerAdsComposable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.ui.cart.CartActivity
import com.d4rk.cartcalculator.ui.dialogs.DeleteCartDialog
import com.d4rk.cartcalculator.ui.dialogs.NewCartDialog
import com.d4rk.cartcalculator.utils.compose.bounceClick
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeComposable() {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val viewModel : HomeViewModel = viewModel()
    val density = LocalDensity.current

    Box(
        modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp , end = 24.dp , bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
        ) {
            Column(
                verticalArrangement = Arrangement.Center ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (viewModel.isLoading.value) {
                    CircularProgressIndicator()
                }
                else if (viewModel.carts.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.no_carts_available)
                    )
                }
                else {
                    LazyColumn(
                        modifier = Modifier
                                .weight(1f)
                                .padding(bottom = viewModel.fabAdHeight.value)
                    ) {
                        items(viewModel.carts) { cart ->
                            CartItemComposable(cart , onDelete = {
                                viewModel.cartToDelete.value =
                                        it; viewModel.showDeleteCartDialog.value = true
                            } , onCardClick = {
                                val intent = Intent(context , CartActivity::class.java)
                                intent.putExtra("cartId" , cart.cartId)
                                context.startActivity(intent)
                            } , viewModel = viewModel)
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            ExtendedFloatingActionButton(modifier = Modifier
                    .bounceClick()
                    .onGloballyPositioned { coordinates ->
                        viewModel.setFabHeight(with(density) { coordinates.size.height.toDp() })
                    }
                    .align(Alignment.End) ,
                                         text = { Text(stringResource(R.string.add_new_cart)) } ,
                                         onClick = { viewModel.openDialog.value = true } ,
                                         icon = {
                                             Icon(
                                                 Icons.Outlined.AddShoppingCart ,
                                                 contentDescription = null
                                             )
                                         })
            BannerAdsComposable(modifier = Modifier.padding(top = 12.dp) , dataStore = dataStore)
        }

        if (viewModel.showSnackbar.value) {
            Snackbar(action = {
                TextButton(onClick = { viewModel.showSnackbar.value = false }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            } , modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomCenter) , content = {
                Text(text = viewModel.snackbarMessage.value)
            })
        }
    }

    if (viewModel.openDialog.value) {
        NewCartDialog(onDismiss = { viewModel.openDialog.value = false } ,
                      onCartCreated = { cartId , cartName ->
                          val cart = ShoppingCartTable(
                              cartId = cartId.toInt() , name = cartName , date = Date()
                          )
                          viewModel.addCart(cart)
                      })
    }

    if (viewModel.showDeleteCartDialog.value) {
        DeleteCartDialog(cart = viewModel.cartToDelete.value ,
                         onDismiss = { viewModel.showDeleteCartDialog.value = false } ,
                         onDeleteConfirmed = {
                             viewModel.deleteCart(it) { success ->
                                 viewModel.showSnackbar.value = true
                                 viewModel.snackbarMessage.value = if (success) {
                                     context.getString(R.string.snackbar_cart_deleted_success)
                                 }
                                 else {
                                     context.getString(R.string.snackbar_cart_deleted_error)
                                 }
                             }
                         })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemComposable(
    cart : ShoppingCartTable ,
    onDelete : (ShoppingCartTable) -> Unit ,
    onCardClick : () -> Unit ,
    viewModel : HomeViewModel
) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy" , Locale.getDefault())
    val dateString = dateFormat.format(cart.date)

    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
            ! viewModel.showDeleteCartDialog.value
        }
        else {
            true
        }
    })

    LaunchedEffect(key1 = dismissState.targetValue) {
        if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
            onDelete(cart)
        }
        else if (! viewModel.showDeleteCartDialog.value && dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(state = dismissState , backgroundContent = {} , content = {
        OutlinedCard(shape = RoundedCornerShape(12.dp) ,
                     modifier = Modifier
                             .fillMaxWidth()
                             .padding(top = 8.dp) ,
                     onClick = {
                         onCardClick()
                     }) {
            Box(modifier = Modifier.clip(MaterialTheme.shapes.medium)) {
                Column(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween ,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = cart.name ,
                            style = MaterialTheme.typography.titleMedium ,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(modifier = Modifier.bounceClick() ,
                                   onClick = { onDelete(cart) }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever ,
                                contentDescription = "Delete cart" ,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.created_on , dateString) ,
                        style = MaterialTheme.typography.bodyMedium ,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    })
}