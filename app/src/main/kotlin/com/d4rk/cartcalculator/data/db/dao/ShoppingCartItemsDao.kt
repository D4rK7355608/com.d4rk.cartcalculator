package com.d4rk.cartcalculator.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.d4rk.cartcalculator.data.db.table.ShoppingCartItemsTable

@Dao
interface ShoppingCartItemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingCartItemsTable)

    @Update
    suspend fun update(item: ShoppingCartItemsTable)

    @Delete
    suspend fun delete(item: ShoppingCartItemsTable)

    @Query("SELECT * FROM cart_items")
    fun getAllItems(): LiveData<List<ShoppingCartItemsTable>>

    @Query("SELECT * FROM cart_items WHERE id = :id")
    fun getItemById(id: Int): LiveData<ShoppingCartItemsTable>

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId")
    suspend fun getItemsByCartId(cartId: Int): List<ShoppingCartItemsTable>

    @Query("DELETE FROM cart_items WHERE cartId = :cartId")
    suspend fun deleteItemsFromCart(cartId: Int)
}