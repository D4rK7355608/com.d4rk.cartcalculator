package com.d4rk.cartcalculator.ui.components.dialogs

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cartcalculator.R

@Composable
fun ImportCartAlertDialog(onDismiss : () -> Unit , onImport : (String) -> Unit) {
    var cartLink : String by remember { mutableStateOf(value = "") }

    AlertDialog(onDismissRequest = onDismiss , title = { Text(text = stringResource(id = R.string.import_cart)) } , text = {
        ImportCartAlertDialogContent(cartLink = cartLink , onCartLinkChange = { cartLink = it })
    } , confirmButton = {
        TextButton(onClick = {
            if (cartLink.isNotEmpty()) {
                onImport(cartLink)
                onDismiss()
            }
        }) {
            Text(text = stringResource(id = R.string.button_import))
        }
    } , dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = stringResource(id = android.R.string.cancel))
        }
    })
}

@Composable
fun ImportCartAlertDialogContent(cartLink : String , onCartLinkChange : (String) -> Unit) {
    val view : View = LocalView.current
    val clipboardManager : ClipboardManager = LocalClipboardManager.current
    Column {
        OutlinedTextField(value = cartLink , onValueChange = onCartLinkChange , label = { Text(text = stringResource(id = R.string.paste_cart_link)) } , modifier = Modifier.fillMaxWidth() , maxLines = 1 , trailingIcon = {
            IconButton(modifier = Modifier.bounceClick() , onClick = {
                clipboardManager.getText()?.text?.let { text ->
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    onCartLinkChange(text)
                }
            }) {
                Icon(modifier = Modifier.size(size = ButtonDefaults.IconSize) , imageVector = Icons.Outlined.ContentPaste , contentDescription = stringResource(id = android.R.string.paste))
            }
        })
        Spacer(modifier = Modifier.height(height = 24.dp))
        InfoMessageSection(message = stringResource(id = R.string.import_cart_instructions))
    }
}