package com.d4rk.cartcalculator.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.stringResource
import com.d4rk.cartcalculator.MyApp
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun NewCartDialog(onDismiss : () -> Unit , onCartCreated : (ShoppingCartTable) -> Unit) {
    val newCart = remember { mutableStateOf<ShoppingCartTable?>(null) }
    val lifecycleScope = rememberCoroutineScope()
    AlertDialog(onDismissRequest = onDismiss , text = { NewCartDialogContent(newCart) } , icon = {
        Icon(
            Icons.Outlined.ShoppingCartCheckout , contentDescription = null
        )
    } , // FIXME: Type mismatch: inferred type is ImageVector but (() -> Unit)? was expected
                confirmButton = {
                    TextButton(onClick = {
                        newCart.value?.let { cart ->
                            onCartCreated(cart)
                            lifecycleScope.launch(Dispatchers.IO) {
                                MyApp.database.newCartDao().insert(cart)
                            }
                        }
                        // TODO: After the new shopping cart was added, will be displayed the new cart composable (ShoppingCartComposable)
                    }) {
                        Text(stringResource(android.R.string.ok))
                    }
                } , dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(stringResource(android.R.string.cancel))
            }
        })
}

@Composable
fun NewCartDialogContent(newCart : MutableState<ShoppingCartTable?>) {
    val nameText = remember { mutableStateOf("") }
    val currentDate = Date()
    Column {
        OutlinedTextField(value = nameText.value ,
                          onValueChange = { nameText.value = it } ,
                          label = { Text("Name") })
    }

    newCart.value = ShoppingCartTable(name = nameText.value , date = currentDate)
}