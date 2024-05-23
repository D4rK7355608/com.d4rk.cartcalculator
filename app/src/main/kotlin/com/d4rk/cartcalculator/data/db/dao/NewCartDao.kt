package com.d4rk.cartcalculator.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable

@Dao
interface NewCartDao {
    @Insert
    suspend fun insert(cart: ShoppingCartTable): Long

    @Delete
    suspend fun delete(cart: ShoppingCartTable)

    @Query("SELECT * FROM ShoppingCartTable")
    suspend fun getAll(): List<ShoppingCartTable>

    @Query("SELECT * FROM ShoppingCartTable WHERE cartId = :cartId")
    suspend fun getCartById(cartId: Int): ShoppingCartTable
}