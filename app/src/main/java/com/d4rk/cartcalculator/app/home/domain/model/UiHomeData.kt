package com.d4rk.cartcalculator.app.home.domain.model

import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

data class UiHomeData(
    val carts : MutableList<ShoppingCartTable> = mutableListOf() ,
    val cartToDelete : ShoppingCartTable? = null ,
    val showCreateCartDialog : Boolean = false ,
    val showImportDialog : Boolean = false ,
    val showDeleteCartDialog : Boolean = false ,
    val showSnackbar : Boolean = false ,
    val shareCartLink : String? = null ,
    val cartToRename : ShoppingCartTable? = null ,
    val showRenameCartDialog : Boolean = false ,
    val currentSort : SortOption = SortOption.DEFAULT ,
    val shareLink : String? = null ,
    val snackbarMessage : UiSnackbar? = null
)