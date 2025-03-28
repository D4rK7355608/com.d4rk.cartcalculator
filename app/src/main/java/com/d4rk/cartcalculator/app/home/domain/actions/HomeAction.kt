package com.d4rk.cartcalculator.app.home.domain.actions

import com.d4rk.cartcalculator.app.home.domain.model.SortOption
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

sealed class HomeAction {
    data object LoadCarts : HomeAction()
    data class AddCart(val cart : ShoppingCartTable) : HomeAction()
    data class DeleteCart(val cart : ShoppingCartTable) : HomeAction()
    data class ImportSharedCart(val encodedData : String) : HomeAction()
    data class OpenCart(val cart : ShoppingCartTable) : HomeAction()
    data class ToggleImportDialog(val isOpen : Boolean) : HomeAction()
    data object OpenNewCartDialog : HomeAction()
    data object DismissNewCartDialog : HomeAction()
    data class OpenDeleteCartDialog(val cart : ShoppingCartTable) : HomeAction()
    data object DismissDeleteCartDialog : HomeAction()
    data class ShowSnackbar(val message : String) : HomeAction()
    data object DismissSnackbar : HomeAction()
    data class GenerateCartShareLink(val cart : ShoppingCartTable) : HomeAction()
    data class RenameCart(val cart : ShoppingCartTable , val newName : String) : HomeAction()
    data object DismissRenameCartDialog : HomeAction()
    data class OpenRenameCartDialog(val cart : ShoppingCartTable) : HomeAction()
    data class SortCarts(val sortOption : SortOption) : HomeAction() // New action
}