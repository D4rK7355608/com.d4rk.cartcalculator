package com.d4rk.cartcalculator.app.home.ui.components.effects

import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.home.domain.actions.HomeAction
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
        ImportCartAlertDialog(onDismiss = { viewModel.sendEvent(event = HomeAction.ToggleImportDialog(isOpen = false)) } , onImport = { cartLink -> viewModel.sendEvent(event = HomeAction.ImportSharedCart(encodedData = cartLink)) })
    }

    // New Cart Dialog
    if (screenState.data?.showCreateCartDialog == true) {
        AddNewCartAlertDialog(onDismiss = { viewModel.sendEvent(event = HomeAction.DismissNewCartDialog) } , onCartCreated = { cart -> viewModel.sendEvent(event = HomeAction.AddCart(cart = cart)) })
    }

    // Delete Cart Dialog
    if (screenState.data?.showDeleteCartDialog == true) {
        DeleteCartAlertDialog(cart = screenState.data?.cartToDelete , onDismiss = { viewModel.sendEvent(event = HomeAction.DismissDeleteCartDialog) } , onDeleteConfirmed = { cart -> viewModel.sendEvent(event = HomeAction.DeleteCart(cart = cart)) })
    }

    // Rename Cart Dialog
    if (screenState.data?.showRenameCartDialog == true && screenState.data?.cartToRename != null) {
        RenameCartAlertDialog(initialName = screenState.data?.cartToRename?.name ?: "" , onDismiss = { viewModel.sendEvent(HomeAction.DismissRenameCartDialog) } , onCartRenamed = { newName -> viewModel.sendEvent(HomeAction.RenameCart(screenState.data?.cartToRename !! , newName)) })
    }
}