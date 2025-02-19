package com.d4rk.cartcalculator.ui.components.dialogs

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
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable

@Composable
fun DeleteCartItemAlertDialog(
    cartItem: ShoppingCartItemsTable?,
    onDismiss: () -> Unit,
    onDeleteConfirmed: (ShoppingCartItemsTable) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss ,
        title = {
            Text(
                text = stringResource(id= R.string.delete_cart_item_title),
            )
        } ,
        text = { DeleteCartItemAlertDialogContent(cartItem = cartItem!!) } ,
        confirmButton = {
            TextButton(onClick = {
                onDeleteConfirmed(cartItem!!)
                onDismiss()
            }) {
                Text(text =stringResource(id = android.R.string.ok))
            }
        } ,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text =stringResource(id = android.R.string.cancel))
            }
        }
    )
}

@Composable
fun DeleteCartItemAlertDialogContent(cartItem: ShoppingCartItemsTable) {
    Column {
        Icon(
            imageVector = Icons.Outlined.RemoveShoppingCart ,
            contentDescription = null ,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally) ,
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id= R.string.delete_cart_item_message),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        InfoMessageSection(message = stringResource(id = R.string.delete_cart_item_warning, cartItem.name))
    }
}