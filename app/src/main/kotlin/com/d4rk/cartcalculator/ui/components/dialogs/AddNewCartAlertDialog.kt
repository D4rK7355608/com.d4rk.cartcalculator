package com.d4rk.cartcalculator.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.MediumVerticalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import java.util.Date

@Composable
fun AddNewCartAlertDialog(onDismiss : () -> Unit , onCartCreated : (ShoppingCartTable) -> Unit) {
    val newCart = remember { mutableStateOf<ShoppingCartTable?>(null) }
    val nameText = remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss ,
                text = { AddNewCartAlertDialogContent(newCart , nameText) } ,
                icon = {
                    Icon(
                        Icons.Outlined.ShoppingCartCheckout , contentDescription = null
                    )
                } ,
                confirmButton = {
                    TextButton(onClick = {
                        newCart.value?.let { cart ->
                            onCartCreated(cart)
                            onDismiss()
                        }
                    }) {
                        Text(text = stringResource(android.R.string.ok))
                    }
                } ,
                dismissButton = {
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text(text = stringResource(android.R.string.cancel))
                    }
                })
}

@Composable
fun AddNewCartAlertDialogContent(
    newCart : MutableState<ShoppingCartTable?> ,
    nameText : MutableState<String> ,
) {
    val currentDate = Date()
    val defaultName = stringResource(id = R.string.shopping_cart)

    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        OutlinedTextField(value = nameText.value ,
                          singleLine = true ,
                          onValueChange = { nameText.value = it } ,
                          label = { Text(text = stringResource(id = R.string.cart_name)) } ,
                          keyboardOptions = KeyboardOptions(
                              capitalization = KeyboardCapitalization.Sentences ,
                              imeAction = ImeAction.Done
                          ) ,
                          keyboardActions = KeyboardActions(onDone = {
                              newCart.value =
                                      ShoppingCartTable(name = nameText.value.ifEmpty { defaultName } ,
                                                        date = currentDate)
                              keyboardController?.hide()
                          }) ,
                          placeholder = { Text(text = stringResource(id = R.string.shopping_cart)) })
        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info , contentDescription = null)
        MediumVerticalSpacer()
        Text(text = stringResource(id = R.string.summary_cart_dialog))
    }
    newCart.value =
            ShoppingCartTable(name = nameText.value.ifEmpty { defaultName } , date = currentDate)
}