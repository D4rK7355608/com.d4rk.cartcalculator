package com.d4rk.cartcalculator.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable

@Composable
fun AddNewCartItemAlertDialog(
    cartId : Int , onDismiss : () -> Unit , onCartCreated : (ShoppingCartItemsTable) -> Unit
) {
    val newCartItem = remember { mutableStateOf<ShoppingCartItemsTable?>(null) }
    AlertDialog(onDismissRequest = onDismiss , text = {
        AddNewCartItemAlertDialogContent(cartId , newCartItem)
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
            Text(text = stringResource(android.R.string.ok))
        }
    } , dismissButton = {
        TextButton(onClick = {
            onDismiss()
        }) {
            Text(text = stringResource(android.R.string.cancel))
        }
    })
}

@Composable
fun AddNewCartItemAlertDialogContent(
    cartId : Int , newCartItem : MutableState<ShoppingCartItemsTable?>
) {
    val nameText = remember { mutableStateOf(value = "") }
    val priceText = remember { mutableStateOf(value = "") }
    val quantityText = remember { mutableStateOf(value = "") }

    val nameFocusRequester = remember { FocusRequester() }
    val priceFocusRequester = remember { FocusRequester() }
    val quantityFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                          modifier = Modifier.focusRequester(nameFocusRequester))

        OutlinedTextField(value = priceText.value ,
                          singleLine = true ,
                          onValueChange = { priceText.value = it } ,
                          label = { Text(text = stringResource(id = R.string.item_price)) } ,
                          placeholder = { Text(text = stringResource(id = R.string.enter_item_price)) } ,
                          keyboardOptions = KeyboardOptions(
                              keyboardType = KeyboardType.Number , imeAction = ImeAction.Next
                          ) ,
                          keyboardActions = KeyboardActions(onNext = { quantityFocusRequester.requestFocus() }) ,
                          modifier = Modifier.focusRequester(priceFocusRequester)
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
                          modifier = Modifier.focusRequester(quantityFocusRequester)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info , contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(id = R.string.dialog_info_cart_item))
    }

    if (nameText.value.isNotBlank() && priceText.value.isNotBlank() && quantityText.value.isNotBlank()) {
        val price = priceText.value.replace(oldChar = ',' , newChar = '.').toDoubleOrNull()
        val quantity = quantityText.value.toIntOrNull()
        if (price != null && quantity != null) {
            newCartItem.value = ShoppingCartItemsTable(
                cartId = cartId ,
                name = nameText.value ,
                price = price.toString() ,
                quantity = quantity
            )
        }
        else {
            newCartItem.value = null
        }
    }
    else {
        newCartItem.value = null
    }
}