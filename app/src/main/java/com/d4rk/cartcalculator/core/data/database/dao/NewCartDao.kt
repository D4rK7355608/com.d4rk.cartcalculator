package com.d4rk.cartcalculator.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

@Dao
interface NewCartDao {
    @Insert
    suspend fun insert(cart : ShoppingCartTable) : Long

    @Delete
    suspend fun delete(cart : ShoppingCartTable)

    @Query("SELECT * FROM ShoppingCartTable")
    suspend fun getAll() : List<ShoppingCartTable>

    @Query("SELECT * FROM ShoppingCartTable WHERE cartId = :cartId")
    suspend fun getCartById(cartId : Int) : ShoppingCartTable?

    @Update
    suspend fun updateCart(cart : ShoppingCartTable)

    @Query("SELECT * FROM ShoppingCartTable WHERE name LIKE '%' || :query || '%'")
    suspend fun searchCartsByName(query: String): List<ShoppingCartTable>
}