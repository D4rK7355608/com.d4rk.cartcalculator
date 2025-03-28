package com.d4rk.cartcalculator.app.home.ui.components.dialogs

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.cartcalculator.R

@Composable
fun RenameCartAlertDialog(
    initialName : String , onDismiss : () -> Unit , onCartRenamed : (String) -> Unit
) {
    val nameText : MutableState<String> = remember { mutableStateOf(value = initialName) }
    val cartNameDefault = stringResource(id = R.string.shopping_cart)
    BasicAlertDialog(onDismiss = onDismiss , onConfirm = {
        onCartRenamed(nameText.value.ifEmpty { cartNameDefault })
        onDismiss()
    } , onCancel = onDismiss , icon = Icons.Outlined.Edit , title = stringResource(id = R.string.rename_cart) , content = {
        OutlinedTextField(
            value = nameText.value ,
                          singleLine = true ,
                          onValueChange = { nameText.value = it } ,
                          label = { Text(text = stringResource(id = R.string.enter_cart_name)) } ,
                          keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences , imeAction = ImeAction.Done) ,
                          keyboardActions = KeyboardActions(onDone = {
                              onCartRenamed(nameText.value.ifEmpty { cartNameDefault })
                              onDismiss()
                          }) ,
                          placeholder = { Text(text = stringResource(id = R.string.shopping_cart)) })
    } , confirmButtonText = stringResource(id = R.string.rename_cart) , dismissButtonText = stringResource(id = android.R.string.cancel))
}