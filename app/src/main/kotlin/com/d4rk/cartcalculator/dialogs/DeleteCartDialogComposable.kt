package com.d4rk.cartcalculator.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable

@Composable
fun DeleteCartDialog(
    cart: ShoppingCartTable?,
    onDismiss: () -> Unit,
    onDeleteConfirmed: (ShoppingCartTable) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_cart_title)) },
        text = {
            Column {
                Icon(
                    imageVector = Icons.Outlined.RemoveShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.delete_cart_message),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.delete_cart_warning, cart?.name ?: ""))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                onDeleteConfirmed(cart!!)
                onDismiss()
            }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}