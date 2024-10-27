package com.d4rk.cartcalculator.data.model.ui.screens

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable

data class UiHomeModel(
    val carts: MutableList<ShoppingCartTable> = mutableListOf() ,
    val cartToDelete: ShoppingCartTable? = null ,
    val showCreateCartDialog: Boolean = false ,
    val showDeleteCartDialog: Boolean = false ,
    val showSnackbar: Boolean = false ,
    val snackbarMessage: String = "" ,
    val fabAdHeight: Dp = 0.dp ,
)