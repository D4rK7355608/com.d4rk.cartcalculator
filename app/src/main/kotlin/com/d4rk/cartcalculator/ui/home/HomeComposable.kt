package com.d4rk.cartcalculator.ui.home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.ads.BannerAdsComposable
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.store.DataStore
import com.d4rk.cartcalculator.dialogs.NewCartDialog
import com.d4rk.cartcalculator.ui.cart.CartActivity
import com.d4rk.cartcalculator.utils.bounceClick
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeComposable() {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val viewModel : HomeViewModel = viewModel()

    Box(
        modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            if (viewModel.isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            else if (viewModel.carts.isEmpty()) {
                Text(
                    text = "No carts available" ,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(viewModel.carts) { cart ->
                        CartItemComposable(cart , onDelete = { cartToDelete ->
                            viewModel.deleteCart(cartToDelete)
                        } , onCardClick = {
                            val intent = Intent(context , CartActivity::class.java)
                            intent.putExtra("cartId" , cart.cartId)
                            context.startActivity(intent)
                        })
                    }
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
        }

        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            ExtendedFloatingActionButton(modifier = Modifier
                    .bounceClick()
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
    }
}

@Composable
fun CartItemComposable(
    cart : ShoppingCartTable , onDelete : (ShoppingCartTable) -> Unit , onCardClick : () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy" , Locale.getDefault())
    val dateString = dateFormat.format(cart.date)

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

                    IconButton(onClick = { onDelete(cart) }) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteForever ,
                            contentDescription = "Delete cart" ,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Text(
                    text = "Created on $dateString" ,
                    style = MaterialTheme.typography.bodyMedium ,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}