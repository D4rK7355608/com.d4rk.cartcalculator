package com.d4rk.cartcalculator.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable

@Dao
interface NewCartDao {
    @Insert
    suspend fun insert(cart: ShoppingCartTable)

    @Delete
    suspend fun delete(cart : ShoppingCartTable)
}