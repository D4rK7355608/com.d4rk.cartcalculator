package com.d4rk.cartcalculator.app.home.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

@Composable
fun DeleteCartAlertDialog(cart : ShoppingCartTable? , onDismiss : () -> Unit , onDeleteConfirmed : (ShoppingCartTable) -> Unit) {
    BasicAlertDialog(onDismiss = onDismiss , onConfirm = {
        cart?.let { onDeleteConfirmed(it) }
        onDismiss()
    } , onCancel = onDismiss , icon = Icons.Outlined.RemoveShoppingCart , title = stringResource(id = R.string.delete_cart_question) , content = {
        Text(text = stringResource(id = R.string.delete_cart_warning , cart?.name ?: ""))
    } , confirmButtonText = stringResource(id = R.string.delete) , dismissButtonText = stringResource(id = android.R.string.cancel))
}