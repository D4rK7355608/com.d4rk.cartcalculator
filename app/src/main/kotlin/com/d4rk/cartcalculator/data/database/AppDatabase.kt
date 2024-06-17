package com.d4rk.cartcalculator.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.d4rk.cartcalculator.data.database.dao.NewCartDao
import com.d4rk.cartcalculator.data.database.dao.ShoppingCartItemsDao
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.repository.DateConverter

@Database(entities = [ShoppingCartTable::class, ShoppingCartItemsTable::class], version = 2)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newCartDao(): NewCartDao
    abstract fun shoppingCartItemsDao(): ShoppingCartItemsDao
}