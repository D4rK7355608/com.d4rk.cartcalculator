package com.d4rk.cartcalculator.app.cart.ui.components.effects

import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.cart.ui.CartViewModel
import com.d4rk.cartcalculator.app.cart.ui.components.dialogs.AddNewCartItemAlertDialog
import com.d4rk.cartcalculator.app.cart.ui.components.dialogs.DeleteCartItemAlertDialog

@Composable
fun CartScreenDialogs(screenState : UiStateScreen<UiCartScreen> , viewModel : CartViewModel) {
    val uiCartScreen : UiCartScreen = screenState.data ?: return

    // Add New Item Dialog
    if (uiCartScreen.openDialog) {
        AddNewCartItemAlertDialog(cartId = uiCartScreen.cart?.cartId ?: 0 , onDismiss = { viewModel.sendEvent(event = CartAction.OpenNewCartItemDialog(isOpen = false)) } , onCartCreated = { cartItem ->
            viewModel.sendEvent(event = CartAction.AddCartItem(cartId = uiCartScreen.cart?.cartId ?: 0 , item = cartItem))
        })
    }

    // Edit Item Dialog
    if (uiCartScreen.openEditDialog && uiCartScreen.currentCartItemForEdit != null) {
        AddNewCartItemAlertDialog(
            cartId = uiCartScreen.cart?.cartId ?: 0 ,
                                  existingCartItem = uiCartScreen.currentCartItemForEdit ,
                                  onDismiss = { viewModel.sendEvent(event = CartAction.OpenEditDialog(item = null)) } ,
                                  onCartCreated = { updatedCartItem -> viewModel.sendEvent(event = CartAction.UpdateCartItem(item = updatedCartItem)) })
    }

    // Delete Item Dialog
    if (uiCartScreen.openDeleteDialog && uiCartScreen.currentCartItemForDeletion != null) {
        DeleteCartItemAlertDialog(cartItem = uiCartScreen.currentCartItemForDeletion , onDismiss = { viewModel.sendEvent(event = CartAction.OpenDeleteDialog(item = null)) } , onDeleteConfirmed = { cartItem -> viewModel.sendEvent(event = CartAction.DeleteCartItem(item = cartItem)) })
    }
}