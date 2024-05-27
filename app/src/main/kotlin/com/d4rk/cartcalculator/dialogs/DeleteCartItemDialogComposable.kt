package com.d4rk.cartcalculator.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
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
import com.d4rk.cartcalculator.data.db.table.ShoppingCartItemsTable

@Composable
fun DeleteCartItemDialog(
    cartItem: ShoppingCartItemsTable,
    onDismiss: () -> Unit,
    onDeleteConfirmed: (ShoppingCartItemsTable) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.delete_cart_item_title),
            )
        },
        text = { DeleteCartItemDialogContent(cartItem) },
        confirmButton = {
            TextButton(onClick = {
                onDeleteConfirmed(cartItem)
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

@Composable
fun DeleteCartItemDialogContent(cartItem: ShoppingCartItemsTable) {
    Column {
        Icon(
            imageVector = Icons.Outlined.RemoveShoppingCart,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.delete_cart_item_message),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.delete_cart_item_warning, cartItem.name),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}