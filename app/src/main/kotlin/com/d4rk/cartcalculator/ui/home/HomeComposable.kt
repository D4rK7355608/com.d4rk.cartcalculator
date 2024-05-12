package com.d4rk.cartcalculator.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.MyApp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable
import com.d4rk.cartcalculator.dialogs.NewCartDialog
import com.d4rk.cartcalculator.utils.bounceClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeComposable() {
    val openDialog = remember { mutableStateOf(false) }
    val carts = remember { mutableStateListOf<ShoppingCartTable>() }
    val lifecycleScope = rememberCoroutineScope()

    LaunchedEffect(key1 = "loadCarts") {
        lifecycleScope.launch {
            val loadedCarts = MyApp.database.newCartDao().getAll()
            carts.addAll(loadedCarts)
        }
    }

    Box(
        modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
    ) {

        if (carts.isEmpty()) {
            Text(
                text = "No carts available" , modifier = Modifier.align(Alignment.Center)
            )
        }
        else {
            LazyColumn {
                items(carts) { cart ->
                    CartItemComposable(cart , onDelete = { cartToDelete ->
                        carts.remove(cartToDelete)
                        lifecycleScope.launch(Dispatchers.IO) {
                            MyApp.database.newCartDao().delete(cartToDelete)
                        }
                    })
                }
            }
        }

        if (openDialog.value) {
            NewCartDialog(onDismiss = { openDialog.value = false } , onCartCreated = { cart ->
                carts.add(cart)
                openDialog.value = false
            })
        }

        ExtendedFloatingActionButton(modifier = Modifier
                .bounceClick()
                .align(Alignment.BottomEnd) ,
                                     text = { Text(stringResource(R.string.add_new_cart)) } ,
                                     onClick = {
                                         openDialog.value = true
                                     } ,
                                     icon = {
                                         Icon(
                                             Icons.Outlined.AddShoppingCart ,
                                             contentDescription = null
                                         )
                                     })
    }
}

@Composable
fun CartItemComposable(cart : ShoppingCartTable , onDelete : (ShoppingCartTable) -> Unit) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy" , Locale.getDefault())
    val dateString = dateFormat.format(cart.date)

    OutlinedCard(
        modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
    ) {
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