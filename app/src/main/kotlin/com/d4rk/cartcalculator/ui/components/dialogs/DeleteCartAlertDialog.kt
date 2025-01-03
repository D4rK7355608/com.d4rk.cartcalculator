package com.d4rk.cartcalculator.ui.components.dialogs

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
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.ui.components.spacers.MediumVerticalSpacer

@Composable
fun DeleteCartAlertDialog(
    cart : ShoppingCartTable ?,
    onDismiss : () -> Unit ,
    onDeleteConfirmed : (ShoppingCartTable) -> Unit ,
) {
    AlertDialog(onDismissRequest = {
        onDismiss() } , title = {
        Text(
            text = stringResource(id= R.string.delete_cart_title) ,
        )
    } , text = { DeleteCartAlertDialogContent(cart) } , confirmButton = {
        TextButton(onClick = {
            onDeleteConfirmed(cart !!)
            onDismiss()
        }) {
            Text(text =stringResource(id = android.R.string.ok))
        }
    } , dismissButton = {
        TextButton(onClick = {
            onDismiss() }) {
            Text(text =stringResource(id = android.R.string.cancel))
        }
    })
}

@Composable
fun DeleteCartAlertDialogContent(cart : ShoppingCartTable?) {
    Column {
        Icon(
            imageVector = Icons.Outlined.RemoveShoppingCart ,
            contentDescription = null ,
            modifier = Modifier.align(Alignment.CenterHorizontally) ,
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        Text(
            text = stringResource(id= R.string.delete_cart_message) ,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        Icon(imageVector = Icons.Outlined.Info , contentDescription = null)
        MediumVerticalSpacer()
        Text(
            text = stringResource(id= R.string.delete_cart_warning , cart?.name ?: "") ,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}