package com.d4rk.cartcalculator.data.database.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Data class representing a shopping cart item in the database.
 *
 * @property itemId The unique identifier of the item in the shopping cart.
 * @property cartId The identifier of the shopping cart the item belongs to.
 * @property name The name of the item.
 * @property price The price of the item.
 * @property quantity The quantity of the item in the shopping cart.
 */
@Serializable
@Entity
data class ShoppingCartItemsTable(
    @PrimaryKey(autoGenerate = true)
    var itemId: Int = 0,
    var cartId: Int,
    val name: String,
    val price: String,
    var quantity: Int,
    var isChecked: Boolean = false
)