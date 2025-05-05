package com.d4rk.cartcalculator.app.cart.domain.model

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import kotlinx.serialization.Serializable

@Serializable
data class UiCartScreen(
    val selectedCurrency : String = "" ,
    val totalPrice : Double = 0.0 ,
    val cartItems : List<ShoppingCartItemsTable> = emptyList() ,
    val cart : ShoppingCartTable? = null ,
    val currentCartItemForEdit : ShoppingCartItemsTable? = null ,
    val openDialog : Boolean = false ,
    val openClearAllDialog : Boolean = false ,
    val openDeleteDialog : Boolean = false ,
    val openEditDialog : Boolean = false ,
    val currentCartItemForDeletion : ShoppingCartItemsTable? = null ,
    val itemQuantities : Map<Int , Int> = emptyMap() ,
    val shareCartLink : String? = null
)