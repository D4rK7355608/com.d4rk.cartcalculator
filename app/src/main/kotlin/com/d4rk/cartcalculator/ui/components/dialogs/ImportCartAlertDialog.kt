package com.d4rk.cartcalculator.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource

@Composable
fun ImportCartAlertDialog(onDismiss : () -> Unit , onImport : (String) -> Unit) {
    var cartLink : String by remember { mutableStateOf(value = "") }

    AlertDialog(onDismissRequest = onDismiss , title = { Text(text = "Import Cart") } , text = {
        ImportCartAlertDialogContent(cartLink = cartLink , onCartLinkChange = { cartLink = it })
    } , confirmButton = {
        TextButton(onClick = {
            if (cartLink.isNotEmpty()) {
                onImport(cartLink)
                onDismiss()
            }
        }) {
            Text(text = "Import")
        }
    } , dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = stringResource(id = android.R.string.cancel))
        }
    })
}

@Composable
fun ImportCartAlertDialogContent(cartLink : String , onCartLinkChange : (String) -> Unit) {
    val clipboardManager : ClipboardManager = LocalClipboardManager.current

    Column {
        OutlinedTextField(value = cartLink , onValueChange = onCartLinkChange , label = { Text("Paste Cart Link") } , modifier = Modifier.fillMaxWidth() , maxLines = 1 , trailingIcon = {
            IconButton(onClick = {
                clipboardManager.getText()?.text?.let { text ->
                    onCartLinkChange(text)
                }
            }) {
                Icon(modifier = Modifier.size(size = ButtonDefaults.IconSize) , imageVector = Icons.Outlined.ContentPaste , contentDescription = stringResource(id = android.R.string.paste))
            }
        })
    }
}