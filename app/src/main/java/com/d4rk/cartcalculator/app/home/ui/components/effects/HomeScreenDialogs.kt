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
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

@Composable
fun HomeScreenDialogs(screenState : UiStateScreen<UiHomeData> , viewModel : HomeViewModel) {

    // Import Cart Dialog
    screenState.data?.showImportDialog?.let { showImportDialog ->
        if (showImportDialog == true) {
            ImportCartAlertDialog(onDismiss = { viewModel.onEvent(event = HomeEvent.ToggleImportDialog(isOpen = false)) } , onImport = { cartLink : String -> viewModel.onEvent(event = HomeEvent.ImportSharedCart(encodedData = cartLink)) })
        }
    }

    // New Cart Dialog
    screenState.data?.showCreateCartDialog?.let { isNewCartDialogVisible ->
        if (isNewCartDialogVisible == true) {
            AddNewCartAlertDialog(onDismiss = { viewModel.onEvent(event = HomeEvent.DismissNewCartDialog) } , onCartCreated = { cart : ShoppingCartTable ->
                viewModel.onEvent(event = HomeEvent.AddCart(cart = cart))
            })
        }
    }

    // Delete Cart Dialog
    screenState.data?.showDeleteCartDialog?.let { showDeleteCartDialog ->
        if (showDeleteCartDialog == true) {
            DeleteCartAlertDialog(cart = screenState.data?.cartToDelete , onDismiss = { viewModel.onEvent(event = HomeEvent.DismissDeleteCartDialog) } , onDeleteConfirmed = { cart : ShoppingCartTable -> viewModel.onEvent(event = HomeEvent.DeleteCart(cart = cart)) })
        }
    }

    // Rename Cart Dialog
    screenState.data?.showRenameCartDialog?.let { isShowingRenameDialog ->
        screenState.data?.cartToRename?.let { renamedCart ->
            if (isShowingRenameDialog) {
                RenameCartAlertDialog(initialName = screenState.data?.cartToRename?.name ?: "" , onDismiss = { viewModel.onEvent(event = HomeEvent.DismissRenameCartDialog) } , onCartRenamed = { newName : String ->
                    screenState.data?.cartToRename?.let { cartToRename ->
                        viewModel.onEvent(event = HomeEvent.RenameCart(cart = cartToRename , newName = newName))
                    }
                })
            }
        }
    }
}