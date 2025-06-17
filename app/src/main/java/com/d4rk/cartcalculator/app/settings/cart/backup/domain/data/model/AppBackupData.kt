package com.d4rk.cartcalculator.app.settings.cart.backup.domain.data.model


import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import kotlinx.serialization.Serializable

@Serializable
data class AppBackupData(
    val events: List<ShoppingCartTable>,
    val eventDetails: List<ShoppingCartItemsTable>
)