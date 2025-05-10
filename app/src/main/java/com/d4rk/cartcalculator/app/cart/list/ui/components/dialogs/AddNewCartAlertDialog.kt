package com.d4rk.cartcalculator.app.cart.list.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.MediumVerticalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import java.util.Date

@Composable
fun AddNewCartAlertDialog(onDismiss : () -> Unit , onCartCreated : (ShoppingCartTable) -> Unit) {
    val newCart : MutableState<ShoppingCartTable?> = remember { mutableStateOf(value = null) }
    val nameText : MutableState<String> = remember { mutableStateOf(value = "") }

    BasicAlertDialog(onDismiss = onDismiss , onConfirm = {
        newCart.value?.let { cart : ShoppingCartTable ->
            onCartCreated(cart)
            onDismiss()
        }
    } , onCancel = onDismiss , icon = Icons.Outlined.ShoppingCartCheckout , title = stringResource(id = R.string.create_a_new_cart) , content = {
        AddNewCartAlertDialogContent(newCart , nameText)
    } , confirmButtonText = stringResource(id = R.string.create))
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