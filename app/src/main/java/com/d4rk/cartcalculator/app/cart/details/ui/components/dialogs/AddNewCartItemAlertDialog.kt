package com.d4rk.cartcalculator.app.cart.details.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.MediumVerticalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

@Composable
fun AddNewCartItemAlertDialog(
    cartId : Int , onDismiss : () -> Unit , onCartCreated : (ShoppingCartItemsTable) -> Unit , existingCartItem : ShoppingCartItemsTable? = null
) {
    val newCartItem : MutableState<ShoppingCartItemsTable?> = remember { mutableStateOf(value = existingCartItem) }

    BasicAlertDialog(onDismiss = onDismiss , onConfirm = {
        newCartItem.value?.let {
            onCartCreated(it)
            onDismiss()
        }
    } , icon = Icons.Outlined.ShoppingBag , title = stringResource(id = if (existingCartItem == null) R.string.add_cart_item else R.string.edit_cart_item) , content = {
        AddNewCartItemAlertDialogContent(
            cartId = cartId , existingItem = existingCartItem , onItemChanged = { newCartItem.value = it })
    } , confirmEnabled = newCartItem.value?.name?.isNotBlank() == true)
}

@Composable
fun AddNewCartItemAlertDialogContent(
    cartId : Int , existingItem : ShoppingCartItemsTable? , onItemChanged : (ShoppingCartItemsTable?) -> Unit
) {
    val name : MutableState<String> = rememberSaveable { mutableStateOf(value = existingItem?.name.orEmpty()) }
    val price : MutableState<String> = rememberSaveable { mutableStateOf(value = existingItem?.price.orEmpty()) }
    val quantity : MutableState<String> = rememberSaveable { mutableStateOf(value = existingItem?.quantity?.toString().orEmpty()) }

    val nameFocus : FocusRequester = remember { FocusRequester() }
    val priceFocus : FocusRequester = remember { FocusRequester() }
    val quantityFocus : FocusRequester = remember { FocusRequester() }
    val keyboardController : SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    Column {
        OutlinedTextField(
            value = name.value ,
                          onValueChange = { name.value = it } ,
                          label = { Text(text = stringResource(id = R.string.item_name)) } ,
                          placeholder = { Text(text = stringResource(id = R.string.enter_item_name)) } ,
                          singleLine = true ,
                          keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences , imeAction = ImeAction.Next) ,
                          keyboardActions = KeyboardActions(onNext = { priceFocus.requestFocus() }) ,
                          modifier = Modifier.fillMaxWidth().focusRequester(focusRequester = nameFocus)
        )

        OutlinedTextField(
            value = price.value ,
                          onValueChange = { price.value = it } ,
                          label = { Text(text = stringResource(id = R.string.item_price)) } ,
                          placeholder = { Text(text = stringResource(id = R.string.enter_item_price)) } ,
                          singleLine = true ,
                          keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number , imeAction = ImeAction.Next) ,
                          keyboardActions = KeyboardActions(onNext = { quantityFocus.requestFocus() }) ,
                          modifier = Modifier.fillMaxWidth().focusRequester(focusRequester = priceFocus)
        )

        OutlinedTextField(
            value = quantity.value ,
                          onValueChange = { quantity.value = it } ,
                          label = { Text(text = stringResource(id = R.string.quantity)) } ,
                          placeholder = { Text(text = stringResource(id = R.string.enter_quantity)) } ,
                          singleLine = true ,
                          keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number , imeAction = ImeAction.Done) ,
                          keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }) ,
                          modifier = Modifier.fillMaxWidth().focusRequester(focusRequester = quantityFocus)
        )

        MediumVerticalSpacer()
        InfoMessageSection(message = stringResource(id = R.string.dialog_info_cart_item))
    }

    val parsedPrice : Double = price.value.replace(oldValue = "," , newValue = ".").toDoubleOrNull() ?: 0.0
    val parsedQty : Int = quantity.value.toIntOrNull() ?: 1

    onItemChanged(
        if (name.value.isBlank()) null
        else existingItem?.copy(
            name = name.value , price = parsedPrice.toString() , quantity = parsedQty
        ) ?: ShoppingCartItemsTable(
            cartId = cartId , name = name.value , price = parsedPrice.toString() , quantity = parsedQty
        )
    )
}