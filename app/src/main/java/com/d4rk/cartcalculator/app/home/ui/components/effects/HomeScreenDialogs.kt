package com.d4rk.cartcalculator.app.home.ui.components.effects

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.home.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel
import com.d4rk.cartcalculator.app.home.ui.components.dialogs.AddNewCartAlertDialog
import com.d4rk.cartcalculator.app.home.ui.components.dialogs.DeleteCartAlertDialog
import com.d4rk.cartcalculator.app.home.ui.components.dialogs.ImportCartAlertDialog
import com.d4rk.cartcalculator.app.home.ui.components.dialogs.RenameCartAlertDialog
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.data.datastore.DataStore

@Composable
fun HomeScreenDialogs(screenState : UiStateScreen<UiHomeData> , viewModel : HomeViewModel) {
    val context : Context = LocalContext.current
    val dataStore : DataStore = remember { DataStore.getInstance(context = context) }
    val openAfterCreation : Boolean by dataStore.openCartsAfterCreation.collectAsState(initial = true)

    // Import Cart Dialog
    if (screenState.data?.showImportDialog == true) {
        ImportCartAlertDialog(onDismiss = { viewModel.onEvent(event = HomeEvent.ToggleImportDialog(isOpen = false)) } , onImport = { cartLink : String -> viewModel.onEvent(event = HomeEvent.ImportSharedCart(encodedData = cartLink)) })
    }

    // New Cart Dialog
    if (screenState.data?.showCreateCartDialog == true) {
        AddNewCartAlertDialog(onDismiss = { viewModel.onEvent(event = HomeEvent.DismissNewCartDialog) } , onCartCreated = { cart : ShoppingCartTable ->
            viewModel.onEvent(event = HomeEvent.AddCart(cart = cart))
            if (openAfterCreation) {
                viewModel.onEvent(event = HomeEvent.OpenCart(cart = cart))
            }
        })
    }

    // Delete Cart Dialog
    if (screenState.data?.showDeleteCartDialog == true) {
        DeleteCartAlertDialog(cart = screenState.data?.cartToDelete , onDismiss = { viewModel.onEvent(event = HomeEvent.DismissDeleteCartDialog) } , onDeleteConfirmed = { cart : ShoppingCartTable -> viewModel.onEvent(event = HomeEvent.DeleteCart(cart = cart)) })
    }

    // Rename Cart Dialog
    if (screenState.data?.showRenameCartDialog == true && screenState.data?.cartToRename != null) {
        RenameCartAlertDialog(initialName = screenState.data?.cartToRename?.name ?: "" , onDismiss = { viewModel.onEvent(event = HomeEvent.DismissRenameCartDialog) } , onCartRenamed = { newName : String ->
            viewModel.onEvent(
                event = HomeEvent.RenameCart(
                    cart = screenState.data?.cartToRename !! , newName = newName
                )
            )
        })
    }
}