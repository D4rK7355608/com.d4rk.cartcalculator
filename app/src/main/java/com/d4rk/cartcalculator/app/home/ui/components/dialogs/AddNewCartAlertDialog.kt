package com.d4rk.cartcalculator.app.home.ui.components.dialogs

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.MediumVerticalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import java.util.Date

@Composable
fun AddNewCartAlertDialog(onDismiss : () -> Unit , onCartCreated : (ShoppingCartTable) -> Unit) {
    val newCart : MutableState<ShoppingCartTable?> = remember { mutableStateOf(value = null) }
    val nameText : MutableState<String> = remember { mutableStateOf(value = "") }
    val view : View = LocalView.current

    AlertDialog(onDismissRequest = onDismiss , title = {
        Text(text = stringResource(id = R.string.create_a_new_cart))
    } , text = {
        AddNewCartAlertDialogContent(newCart , nameText)
    } , icon = {
        Icon(Icons.Outlined.ShoppingCartCheckout , contentDescription = null)
    } , confirmButton = {
        TextButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            newCart.value?.let { cart ->
                onCartCreated(cart)
                onDismiss()
            }
        }) {
            Text(text = stringResource(R.string.create))
        }
    } , dismissButton = {
        TextButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
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
    val defaultName : String = stringResource(id = R.string.shopping_cart)

    val keyboardController : SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    Column {
        OutlinedTextField(
            value = nameText.value ,
                          singleLine = true ,
                          onValueChange = { nameText.value = it } ,
                          label = { Text(text = stringResource(id = R.string.enter_cart_name)) } ,
                          keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences , imeAction = ImeAction.Done) ,
                          keyboardActions = KeyboardActions(onDone = {
                              newCart.value = ShoppingCartTable(name = nameText.value.ifEmpty { defaultName } , date = currentDate.time)

                              keyboardController?.hide()
                          }) ,
                          placeholder = { Text(text = stringResource(id = R.string.shopping_cart)) })
        MediumVerticalSpacer()
        InfoMessageSection(message = stringResource(id = R.string.summary_cart_dialog))
    }
    newCart.value = ShoppingCartTable(name = nameText.value.ifEmpty { defaultName } , date = currentDate.time)
}