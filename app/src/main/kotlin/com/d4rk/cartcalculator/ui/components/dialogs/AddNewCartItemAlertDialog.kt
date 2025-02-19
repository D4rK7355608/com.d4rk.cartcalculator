package com.d4rk.cartcalculator.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable

@Composable
fun AddNewCartItemAlertDialog(
    cartId : Int ,
    onDismiss : () -> Unit ,
    onCartCreated : (ShoppingCartItemsTable) -> Unit ,
    existingCartItem : ShoppingCartItemsTable? = null
) {

    val newCartItem : MutableState<ShoppingCartItemsTable?> =
            remember { mutableStateOf(value = null) }
    val initialName : String = existingCartItem?.name ?: ""
    val initialPrice : String = existingCartItem?.price ?: ""
    val initialQuantity : String = existingCartItem?.quantity?.toString() ?: ""

    AlertDialog(onDismissRequest = onDismiss , text = {
        AddNewCartItemAlertDialogContent(
            cartId = cartId ,
            newCartItem = newCartItem ,
            initialName = initialName ,
            initialPrice = initialPrice ,
            initialQuantity = initialQuantity ,
            existingCartItem = existingCartItem
        )
    } , icon = {
        Icon(
            Icons.Outlined.ShoppingBag , contentDescription = null
        )
    } , confirmButton = {
        TextButton(onClick = {
            newCartItem.value?.let { cartItem ->
                onCartCreated(cartItem)
            }
        } , enabled = newCartItem.value != null) {
            Text(text = stringResource(id = android.R.string.ok))
        }
    } , dismissButton = {
        TextButton(onClick = {
            onDismiss()
        }) {
            Text(text = stringResource(id = android.R.string.cancel))
        }
    })
}

@Composable
fun AddNewCartItemAlertDialogContent(
    cartId : Int ,
    newCartItem : MutableState<ShoppingCartItemsTable?> ,
    initialName : String ,
    initialPrice : String ,
    initialQuantity : String ,
    existingCartItem : ShoppingCartItemsTable?
) {
    val nameText : MutableState<String> = remember { mutableStateOf(value = initialName) }
    val priceText : MutableState<String> = remember { mutableStateOf(value = initialPrice) }
    val quantityText : MutableState<String> = remember { mutableStateOf(value = initialQuantity) }

    val nameFocusRequester : FocusRequester = remember { FocusRequester() }
    val priceFocusRequester : FocusRequester = remember { FocusRequester() }
    val quantityFocusRequester : FocusRequester = remember { FocusRequester() }
    val keyboardController : SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    Column {
        OutlinedTextField(value = nameText.value ,
                          singleLine = true ,
                          onValueChange = { nameText.value = it } ,
                          label = { Text(text = stringResource(id = R.string.item_name)) } ,
                          keyboardOptions = KeyboardOptions(
                              capitalization = KeyboardCapitalization.Sentences ,
                              imeAction = ImeAction.Next
                          ) ,
                          keyboardActions = KeyboardActions(onNext = { priceFocusRequester.requestFocus() }) ,
                          placeholder = { Text(text = stringResource(id = R.string.enter_item_name)) } ,
                          modifier = Modifier.focusRequester(focusRequester = nameFocusRequester))

        OutlinedTextField(value = priceText.value ,
                          singleLine = true ,
                          onValueChange = { priceText.value = it } ,
                          label = { Text(text = stringResource(id = R.string.item_price)) } ,
                          placeholder = { Text(text = stringResource(id = R.string.enter_item_price)) } ,
                          keyboardOptions = KeyboardOptions(
                              keyboardType = KeyboardType.Number , imeAction = ImeAction.Next
                          ) ,
                          keyboardActions = KeyboardActions(onNext = { quantityFocusRequester.requestFocus() }) ,
                          modifier = Modifier.focusRequester(focusRequester = priceFocusRequester)
        )

        OutlinedTextField(value = quantityText.value ,
                          singleLine = true ,
                          onValueChange = { quantityText.value = it } ,
                          label = { Text(text = stringResource(id = R.string.quantity)) } ,
                          placeholder = { Text(text = stringResource(id = R.string.enter_quantity)) } ,
                          keyboardOptions = KeyboardOptions(
                              keyboardType = KeyboardType.Number , imeAction = ImeAction.Done
                          ) ,
                          keyboardActions = KeyboardActions(onDone = {
                              keyboardController?.hide()
                          }) ,
                          modifier = Modifier.focusRequester(focusRequester = quantityFocusRequester)
        )

        Spacer(modifier = Modifier.height(height = 24.dp))
        InfoMessageSection(message = stringResource(id = R.string.dialog_info_cart_item))
    }

    if (nameText.value.isNotBlank() && priceText.value.isNotBlank() && quantityText.value.isNotBlank()) {
        val price : Double? = priceText.value.replace(oldChar = ',' , newChar = '.').toDoubleOrNull()
        val quantity : Int? = quantityText.value.toIntOrNull()
        if (price != null && quantity != null) {
            newCartItem.value = if (existingCartItem != null) {
                val updatedItem = existingCartItem.copy(
                    name = nameText.value , price = price.toString() , quantity = quantity
                )
                updatedItem
            }
            else {
                val newItem = ShoppingCartItemsTable(
                    cartId = cartId ,
                    name = nameText.value ,
                    price = price.toString() ,
                    quantity = quantity
                )
                newItem
            }
        }
        else {
            newCartItem.value = null
        }
    }
    else {
        newCartItem.value = null
    }
}