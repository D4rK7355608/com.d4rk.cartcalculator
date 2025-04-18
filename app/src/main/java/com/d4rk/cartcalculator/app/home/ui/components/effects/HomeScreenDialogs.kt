package com.d4rk.cartcalculator.app.home.ui.components.effects

import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.home.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel
import com.d4rk.cartcalculator.app.home.ui.components.dialogs.AddNewCartAlertDialog
import com.d4rk.cartcalculator.app.home.ui.components.dialogs.DeleteCartAlertDialog
import com.d4rk.cartcalculator.app.home.ui.components.dialogs.ImportCartAlertDialog
import com.d4rk.cartcalculator.app.home.ui.components.dialogs.RenameCartAlertDialog

@Composable
fun HomeScreenDialogs(screenState : UiStateScreen<UiHomeData> , viewModel : HomeViewModel) {
    // Import Cart Dialog
    if (screenState.data?.showImportDialog == true) {
        ImportCartAlertDialog(onDismiss = { viewModel.onEvent(event = HomeEvent.ToggleImportDialog(isOpen = false)) } , onImport = { cartLink -> viewModel.onEvent(event = HomeEvent.ImportSharedCart(encodedData = cartLink)) })
    }

    // New Cart Dialog
    if (screenState.data?.showCreateCartDialog == true) {
        AddNewCartAlertDialog(onDismiss = { viewModel.onEvent(event = HomeEvent.DismissNewCartDialog) } , onCartCreated = { cart -> viewModel.onEvent(event = HomeEvent.AddCart(cart = cart)) })
    }

    // Delete Cart Dialog
    if (screenState.data?.showDeleteCartDialog == true) {
        DeleteCartAlertDialog(cart = screenState.data?.cartToDelete , onDismiss = { viewModel.onEvent(event = HomeEvent.DismissDeleteCartDialog) } , onDeleteConfirmed = { cart -> viewModel.onEvent(event = HomeEvent.DeleteCart(cart = cart)) })
    }

    // Rename Cart Dialog
    if (screenState.data?.showRenameCartDialog == true && screenState.data?.cartToRename != null) {
        RenameCartAlertDialog(initialName = screenState.data?.cartToRename?.name ?: "" , onDismiss = { viewModel.onEvent(HomeEvent.DismissRenameCartDialog) } , onCartRenamed = { newName -> viewModel.onEvent(HomeEvent.RenameCart(screenState.data?.cartToRename !! , newName)) })
    }
}