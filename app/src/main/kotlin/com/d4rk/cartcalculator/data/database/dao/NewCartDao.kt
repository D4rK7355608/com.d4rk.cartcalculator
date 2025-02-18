package com.d4rk.cartcalculator.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable

@Dao
interface NewCartDao {
    @Insert
    suspend fun insert(cart: ShoppingCartTable): Long

    @Delete
    suspend fun delete(cart: ShoppingCartTable)

    @Query("SELECT * FROM ShoppingCartTable")
    suspend fun getAll(): List<ShoppingCartTable>

    @Query("SELECT * FROM ShoppingCartTable WHERE cartId = :cartId")
    suspend fun getCartById(cartId: Int): ShoppingCartTable?
}