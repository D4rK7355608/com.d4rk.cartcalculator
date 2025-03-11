package com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components.dialogs

import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.main.ui.routes.home.ui.HomeViewModel

@Composable
fun HomeDialogs(screenState: UiStateScreen<UiHomeData> , viewModel: HomeViewModel) {
    // Import Cart Dialog
    if (screenState.data?.showImportDialog == true) {
        ImportCartAlertDialog(
            onDismiss = { viewModel.sendEvent(HomeAction.ToggleImportDialog(false)) } ,
            onImport = { cartLink -> viewModel.sendEvent(HomeAction.ImportSharedCart(cartLink)) }
        )
    }

    // New Cart Dialog
    if (screenState.data?.showCreateCartDialog == true) {
        AddNewCartAlertDialog(
            onDismiss = { viewModel.sendEvent(HomeAction.DismissNewCartDialog) } ,
            onCartCreated = { cart -> viewModel.sendEvent(HomeAction.AddCart(cart)) }
        )
    }

    // Delete Cart Dialog
    if (screenState.data?.showDeleteCartDialog == true) {
        DeleteCartAlertDialog(
            cart = screenState.data?.cartToDelete ,
            onDismiss = { viewModel.sendEvent(HomeAction.DismissDeleteCartDialog) } ,
            onDeleteConfirmed = { cart -> viewModel.sendEvent(HomeAction.DeleteCart(cart)) }
        )
    }
}