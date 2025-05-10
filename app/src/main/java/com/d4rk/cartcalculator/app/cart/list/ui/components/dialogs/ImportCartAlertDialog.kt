package com.d4rk.cartcalculator.app.cart.list.ui.components.dialogs

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.MediumVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.utils.extensions.isValidShareLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ImportCartAlertDialog(onDismiss : () -> Unit , onImport : (String) -> Unit) {
    var cartLink : String by remember { mutableStateOf(value = "") }
    val isValidLink : Boolean by remember(key1 = cartLink) { derivedStateOf { cartLink.isValidShareLink() } }

    BasicAlertDialog(onDismiss = onDismiss , onConfirm = {
        onImport(cartLink)
        onDismiss()
    } , onCancel = onDismiss , icon = Icons.Outlined.ImportExport , title = stringResource(id = R.string.import_shared_cart) , content = {
        ImportCartAlertDialogContent(cartLink = cartLink , onCartLinkChange = { cartLink = it })
    } , confirmButtonText = stringResource(id = R.string.button_import) , confirmEnabled = isValidLink)
}

@Composable
fun ImportCartAlertDialogContent(cartLink : String , onCartLinkChange : (String) -> Unit) {
    val view : View = LocalView.current
    val clipboard : Clipboard = LocalClipboard.current
    val coroutineScope : CoroutineScope = rememberCoroutineScope()

    Column {
        OutlinedTextField(value = cartLink , onValueChange = onCartLinkChange , label = { Text(text = stringResource(id = R.string.paste_cart_link)) } , modifier = Modifier.fillMaxWidth() , maxLines = 1 , trailingIcon = {
            IconButton(modifier = Modifier.bounceClick() , onClick = {
                coroutineScope.launch {
                    clipboard.getClipEntry()?.let { clipEntry : ClipEntry ->
                        if (clipEntry.clipData.itemCount > 0) {
                            clipEntry.clipData.getItemAt(0)?.let { textFieldValue ->
                                textFieldValue.text?.let {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    onCartLinkChange(it.toString())
                                }
                            }
                        }
                    }
                }
            }) {
                Icon(
                    modifier = Modifier.size(size = SizeConstants.ButtonIconSize) , imageVector = Icons.Outlined.ContentPaste , contentDescription = stringResource(id = android.R.string.paste)
                )
            }
        })
        MediumVerticalSpacer()
        InfoMessageSection(message = stringResource(id = R.string.import_cart_instructions))
    }
}