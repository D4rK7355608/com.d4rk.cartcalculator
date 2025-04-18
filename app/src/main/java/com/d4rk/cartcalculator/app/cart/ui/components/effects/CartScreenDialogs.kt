package com.d4rk.cartcalculator.app.cart.ui.components.effects

import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.cart.domain.actions.CartEvent
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.cart.ui.CartViewModel
import com.d4rk.cartcalculator.app.cart.ui.components.dialogs.AddNewCartItemAlertDialog
import com.d4rk.cartcalculator.app.cart.ui.components.dialogs.DeleteCartItemAlertDialog

@Composable
fun CartScreenDialogs(screenState : UiStateScreen<UiCartScreen> , viewModel : CartViewModel) {
    val uiCartScreen : UiCartScreen = screenState.data ?: return

    // Add New Item Dialog
    if (uiCartScreen.openDialog) {
        AddNewCartItemAlertDialog(cartId = uiCartScreen.cart?.cartId ?: 0 , onDismiss = { viewModel.onEvent(event = CartEvent.OpenNewCartItemDialog(isOpen = false)) } , onCartCreated = { cartItem ->
            viewModel.onEvent(event = CartEvent.AddCartItem(cartId = uiCartScreen.cart?.cartId ?: 0 , item = cartItem))
        })
    }

    // Edit Item Dialog
    if (uiCartScreen.openEditDialog && uiCartScreen.currentCartItemForEdit != null) {
        AddNewCartItemAlertDialog(
            cartId = uiCartScreen.cart?.cartId ?: 0 ,
                                  existingCartItem = uiCartScreen.currentCartItemForEdit ,
                                  onDismiss = { viewModel.onEvent(event = CartEvent.OpenEditDialog(item = null)) } ,
                                  onCartCreated = { updatedCartItem -> viewModel.onEvent(event = CartEvent.UpdateCartItem(item = updatedCartItem)) })
    }

    // Delete Item Dialog
    if (uiCartScreen.openDeleteDialog && uiCartScreen.currentCartItemForDeletion != null) {
        DeleteCartItemAlertDialog(cartItem = uiCartScreen.currentCartItemForDeletion , onDismiss = { viewModel.onEvent(event = CartEvent.OpenDeleteDialog(item = null)) } , onDeleteConfirmed = { cartItem -> viewModel.onEvent(event = CartEvent.DeleteCartItem(item = cartItem)) })
    }
}