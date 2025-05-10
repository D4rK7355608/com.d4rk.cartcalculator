package com.d4rk.cartcalculator.app.cart.details.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.cartcalculator.R

@Composable
fun CartClearAllDialog(onDismiss : () -> Unit , onConfirm : () -> Unit) {
    BasicAlertDialog(onDismiss = onDismiss , onConfirm = {
        onConfirm()
        onDismiss()
    } , icon = Icons.Outlined.DeleteSweep , title = stringResource(id = R.string.clear_cart_title) , content = {
        Text(text = stringResource(id = R.string.clear_cart_message))
    })
}