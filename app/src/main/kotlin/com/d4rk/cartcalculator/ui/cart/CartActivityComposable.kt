package com.d4rk.cartcalculator.ui.cart

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.MyApp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.db.table.ShoppingCartItemsTable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartActivityComposable(activity : CartActivity) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val cartItems = remember { mutableStateListOf<ShoppingCartItemsTable>() }


    val cartId = activity.intent.getIntExtra("cartId", 0)

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {

        // TODO: add as name the name of the cart
        LargeTopAppBar(title = { Text(stringResource(R.string.settings)) } , navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null)
            }
        } , actions = {
            IconButton(onClick = {
                // TODO: Open the create new item in cart dialog
            }) {
                Icon(
                    Icons.Outlined.AddShoppingCart , contentDescription = null,
                )
            }
        } , scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        Box(
            modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
        ) {
            if (cartItems.isEmpty()) {
                Text(
                    text = "Your shopping cart is empty" , modifier = Modifier.align(Alignment.Center)
                )
            }
            else {
                LazyColumn {
                    items(cartItems) { cartItem ->
                        CartItemComposable(
                            cartName = cartItem.name,
                            cartPrice = cartItem.price,
                            onMinusClick = { /* Handle minus click */ },
                            quantity = cartItem.quantity,
                            onPlusClick = { /* Handle plus click */ }
                        )
                    }
                }
            }

            ElevatedCard(Modifier.fillMaxWidth()) {

            }
        }
    }
}

@Composable
fun CartItemComposable(
    cartName : String ,
    cartPrice : String ,
    onMinusClick : () -> Unit ,
    quantity : Int ,
    onPlusClick : () -> Unit
) {
    Box(
        modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize() , horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = cartName , style = MaterialTheme.typography.displayMedium)
                Text(text = cartPrice , style = MaterialTheme.typography.bodySmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onMinusClick() }) {
                    Icon(
                        imageVector = Icons.Outlined.RemoveCircleOutline ,
                        contentDescription = "Decrease Quantity"
                    )
                }

                Text(
                    text = quantity.toString() ,
                    style = MaterialTheme.typography.bodySmall ,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                IconButton(onClick = { onPlusClick() }) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircleOutline ,
                        contentDescription = "Increase Quantity"
                    )
                }
            }
        }
    }
}