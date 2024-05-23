package com.d4rk.cartcalculator.data.db.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShoppingCartItemsTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var cartId: Int,
    val name: String,
    val price: String,
    var quantity: Int
)