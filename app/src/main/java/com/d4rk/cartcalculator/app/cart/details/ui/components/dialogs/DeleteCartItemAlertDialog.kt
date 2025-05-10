package com.d4rk.cartcalculator.app.cart.details.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

@Composable
fun DeleteCartItemAlertDialog(cartItem : ShoppingCartItemsTable , onDismiss : () -> Unit , onDeleteConfirmed : (ShoppingCartItemsTable) -> Unit) {
    BasicAlertDialog(onDismiss = onDismiss , onConfirm = {
        onDeleteConfirmed(cartItem)
        onDismiss()
    } , iconTint = MaterialTheme.colorScheme.error , onCancel = onDismiss , icon = Icons.Outlined.RemoveShoppingCart , title = stringResource(id = R.string.delete_cart_item_title) , content = {
        Text(text = stringResource(id = R.string.delete_cart_item_message) + " " + stringResource(id = R.string.delete_cart_item_warning , cartItem?.name ?: ""))
    })
}