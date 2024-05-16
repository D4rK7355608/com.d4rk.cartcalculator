package com.d4rk.cartcalculator.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.data.db.table.ShoppingCartItemsTable

@Composable
fun NewCartItemDialog(
    cartId: Int,
    onDismiss: () -> Unit,
    onCartCreated: (ShoppingCartItemsTable) -> Unit
) { // FIXME: Parameter 'cartId' is never used
    val newCartItem = remember { mutableStateOf<ShoppingCartItemsTable?>(null) }
    AlertDialog(onDismissRequest = onDismiss, text = {
        NewCartItemDialogContent(cartId, newCartItem)
    }, icon = {
        Icon(
            Icons.Outlined.ShoppingBag, contentDescription = null
        )
    }, confirmButton = {
        TextButton(onClick = {
            newCartItem.value?.let { cartItem ->
                onCartCreated(cartItem)
            }
        }) {
            Text(stringResource(android.R.string.ok))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismiss()
        }) {
            Text(stringResource(android.R.string.cancel))
        }
    })
}

@Composable
fun NewCartItemDialogContent(cartId: Int, newCartItem: MutableState<ShoppingCartItemsTable?>) {
    val nameText = remember { mutableStateOf("") }
    val priceText = remember { mutableStateOf("") }
    val quantityText = remember { mutableStateOf("") }

    Column {
        OutlinedTextField(value = nameText.value,
            onValueChange = { nameText.value = it },
            label = { Text("Item Name") },
            placeholder = { Text("Enter item name") })
        OutlinedTextField(value = priceText.value,
            onValueChange = { priceText.value = it },
            label = { Text("Item Price") },
            placeholder = { Text("Enter item price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(value = quantityText.value,
            onValueChange = { quantityText.value = it },
            label = { Text("Quantity") },
            placeholder = { Text("Enter quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Enter the details of the item you want to add to the cart.")
    }
    newCartItem.value = ShoppingCartItemsTable(
        cartId = cartId,
        name = nameText.value,
        price = priceText.value,
        quantity = if (quantityText.value.isNotEmpty()) quantityText.value.toInt() else 0
    )
}