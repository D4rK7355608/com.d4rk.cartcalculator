package com.d4rk.cartcalculator.app.main.ui.routes.home.domain.model

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

data class UiHomeData(
    val carts: MutableList<ShoppingCartTable> = mutableListOf() ,
    val cartToDelete: ShoppingCartTable? = null ,
    val showCreateCartDialog: Boolean = false ,
    val showImportDialog: Boolean = false ,
    val showDeleteCartDialog: Boolean = false ,
    val showSnackbar: Boolean = false ,
    val shareCartLink: String? = null
)