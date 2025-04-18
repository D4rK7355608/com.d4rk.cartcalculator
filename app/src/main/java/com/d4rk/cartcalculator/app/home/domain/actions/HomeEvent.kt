package com.d4rk.cartcalculator.app.home.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import com.d4rk.cartcalculator.app.home.domain.model.SortOption
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

sealed class HomeEvent : UiEvent{
    data object LoadCarts : HomeEvent()
    data class AddCart(val cart : ShoppingCartTable) : HomeEvent()
    data class DeleteCart(val cart : ShoppingCartTable) : HomeEvent()
    data class ImportSharedCart(val encodedData : String) : HomeEvent()
    data class OpenCart(val cart : ShoppingCartTable) : HomeEvent()
    data class ToggleImportDialog(val isOpen : Boolean) : HomeEvent()
    data object OpenNewCartDialog : HomeEvent()
    data object DismissNewCartDialog : HomeEvent()
    data class OpenDeleteCartDialog(val cart : ShoppingCartTable) : HomeEvent()
    data object DismissDeleteCartDialog : HomeEvent()
    data class GenerateCartShareLink(val cart : ShoppingCartTable) : HomeEvent()
    data class RenameCart(val cart : ShoppingCartTable , val newName : String) : HomeEvent()
    data object DismissRenameCartDialog : HomeEvent()
    data class OpenRenameCartDialog(val cart : ShoppingCartTable) : HomeEvent()
    data class SortCarts(val sortOption : SortOption) : HomeEvent()
}