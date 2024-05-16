package com.d4rk.cartcalculator.data.db.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class ShoppingCartItemsTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cartId: Int,
    val name: String,
    val price: String,
    var quantity: Int
)