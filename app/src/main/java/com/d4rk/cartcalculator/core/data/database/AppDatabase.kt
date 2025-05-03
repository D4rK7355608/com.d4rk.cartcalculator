package com.d4rk.cartcalculator.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.d4rk.cartcalculator.core.data.database.dao.NewCartDao
import com.d4rk.cartcalculator.core.data.database.dao.ShoppingCartItemsDao
import com.d4rk.cartcalculator.core.data.database.dto.DateConverter
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

@Database(entities = [ShoppingCartTable::class, ShoppingCartItemsTable::class], version = 3, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newCartDao(): NewCartDao
    abstract fun shoppingCartItemsDao(): ShoppingCartItemsDao
}