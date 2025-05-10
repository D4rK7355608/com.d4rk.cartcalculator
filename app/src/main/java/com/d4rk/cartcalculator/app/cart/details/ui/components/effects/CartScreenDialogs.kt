package com.d4rk.cartcalculator.app.cart.details.ui.components.effects

import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.cart.details.domain.actions.CartEvent
import com.d4rk.cartcalculator.app.cart.details.domain.model.ui.UiCartScreen
import com.d4rk.cartcalculator.app.cart.details.ui.CartViewModel
import com.d4rk.cartcalculator.app.cart.details.ui.components.dialogs.AddNewCartItemAlertDialog
import com.d4rk.cartcalculator.app.cart.details.ui.components.dialogs.CartClearAllDialog
import com.d4rk.cartcalculator.app.cart.details.ui.components.dialogs.DeleteCartItemAlertDialog
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

@Composable
fun CartScreenDialogs(screenState : UiStateScreen<UiCartScreen> , viewModel : CartViewModel) {
    val data : UiCartScreen = screenState.data ?: return
    val cartId : Int = data.cart?.cartId ?: return

    // Add Dialog
    if (data.openDialog) {
        AddNewCartItemAlertDialog(cartId = cartId , onDismiss = { viewModel.onEvent(event = CartEvent.OpenNewCartItemDialog(isOpen = false)) } , onCartCreated = { item -> viewModel.onEvent(event = CartEvent.AddCartItem(cartId = cartId , item = item)) })
    }

    // Edit Dialog
    data.currentCartItemForEdit?.takeIf { data.openEditDialog }?.let { item : ShoppingCartItemsTable ->
        AddNewCartItemAlertDialog(cartId = cartId , existingCartItem = item , onDismiss = { viewModel.onEvent(event = CartEvent.OpenEditDialog(item = null)) } , onCartCreated = { updated -> viewModel.onEvent(event = CartEvent.UpdateCartItem(item = updated)) })
    }

    // Delete Dialog
    data.currentCartItemForDeletion?.takeIf { data.openDeleteDialog }?.let { item : ShoppingCartItemsTable ->
        DeleteCartItemAlertDialog(cartItem = item , onDismiss = { viewModel.onEvent(event = CartEvent.OpenDeleteDialog(item = null)) } , onDeleteConfirmed = { confirmed -> viewModel.onEvent(event = CartEvent.DeleteCartItem(item = confirmed)) })
    }

    // Clear All Dialog
    if (data.openClearAllDialog) {
        CartClearAllDialog(onDismiss = { viewModel.onEvent(event = CartEvent.OpenClearAllDialog(isOpen = false)) } , onConfirm = { viewModel.onEvent(event = CartEvent.ClearAllItems) })
    }
}