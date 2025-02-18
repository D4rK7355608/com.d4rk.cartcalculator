package com.d4rk.cartcalculator.data.model.ui.screens

import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import kotlinx.serialization.Serializable

@Serializable
data class UiCartScreen(
    val selectedCurrency: String = "" ,
    val totalPrice: Double = 0.0 ,
    val cartItems: List<ShoppingCartItemsTable> = emptyList() ,
    val cart: ShoppingCartTable? = null ,
    val currentCartItemForEdit: ShoppingCartItemsTable? = null,
    val openDialog: Boolean = false ,
    val openDeleteDialog: Boolean = false ,
    val openEditDialog: Boolean = false,
    val currentCartItemForDeletion: ShoppingCartItemsTable? = null ,
    val itemQuantities: Map<Int, Int> = emptyMap()
)
