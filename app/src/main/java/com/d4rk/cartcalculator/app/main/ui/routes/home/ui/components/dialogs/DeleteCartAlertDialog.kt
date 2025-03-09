package com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components.dialogs

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

@Composable
fun DeleteCartAlertDialog(
    cart : ShoppingCartTable? ,
    onDismiss : () -> Unit ,
    onDeleteConfirmed : (ShoppingCartTable) -> Unit ,
) {
    val view : View = LocalView.current

    AlertDialog(onDismissRequest = { onDismiss() } , title = {
        Text(text = stringResource(id = R.string.delete_cart_question) , style = MaterialTheme.typography.headlineSmall)
    } , icon = {
        Icon(Icons.Outlined.RemoveShoppingCart , contentDescription = stringResource(id = R.string.delete_cart) , tint = MaterialTheme.colorScheme.error)
    } , text = {
        Text(text = stringResource(id = R.string.delete_cart_warning , cart?.name ?: ""))
    } , confirmButton = {
        TextButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            cart?.let { onDeleteConfirmed(it) }
            onDismiss()
        } , enabled = cart != null) {
            Text(text = stringResource(id = R.string.delete))
        }
    } , dismissButton = {
        TextButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onDismiss()
        }) {
            Text(text = stringResource(id = android.R.string.cancel))
        }
    })
}