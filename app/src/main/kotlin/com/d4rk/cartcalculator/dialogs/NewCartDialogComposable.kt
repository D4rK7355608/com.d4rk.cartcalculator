package com.d4rk.cartcalculator.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.MyApp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun NewCartDialog(onDismiss : () -> Unit , onCartCreated : (Long , String) -> Unit) {
    val newCart = remember { mutableStateOf<ShoppingCartTable?>(null) }
    val lifecycleScope = rememberCoroutineScope()
    val nameText = remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss ,
                text = { NewCartDialogContent(newCart , nameText) } ,
                icon = {
                    Icon(
                        Icons.Outlined.ShoppingCartCheckout , contentDescription = null
                    )
                } ,
                confirmButton = {
                    TextButton(onClick = {
                        newCart.value?.let { cart ->
                            lifecycleScope.launch(Dispatchers.IO) {
                                val cartId = MyApp.database.newCartDao().insert(cart)
                                onCartCreated(cartId , cart.name)
                            }
                        }

                    }) {
                        Text(stringResource(android.R.string.ok))
                    }
                } ,
                dismissButton = {
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                })
}

@Composable
fun NewCartDialogContent(
    newCart : MutableState<ShoppingCartTable?> , nameText : MutableState<String>
) {
    val currentDate = Date()
    val defaultName = stringResource(R.string.shopping_cart)
    Column {
        OutlinedTextField(value = nameText.value ,
                          onValueChange = { nameText.value = it } ,
                          label = { Text("Name") } ,
                          placeholder = { Text(stringResource(R.string.shopping_cart)) })
        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info , contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(R.string.summary_cart_dialog))
    }
    newCart.value =
            ShoppingCartTable(name = nameText.value.ifEmpty { defaultName } , date = currentDate)
}