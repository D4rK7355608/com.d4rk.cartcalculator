package com.d4rk.cartcalculator.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.d4rk.cartcalculator.data.db.dao.NewCartDao
import com.d4rk.cartcalculator.data.db.dao.ShoppingCartItemsDao
import com.d4rk.cartcalculator.data.db.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.repository.DateConverter

@Database(entities = [ShoppingCartTable::class , ShoppingCartItemsTable::class] , version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newCartDao() : NewCartDao
    abstract fun shoppingCartItemsDao() : ShoppingCartItemsDao
}