package com.d4rk.cartcalculator.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.d4rk.cartcalculator.core.data.database.dao.NewCartDao
import com.d4rk.cartcalculator.core.data.database.dao.ShoppingCartItemsDao
import com.d4rk.cartcalculator.core.data.database.dto.DateConverter
import com.d4rk.cartcalculator.core.data.database.migrations.MIGRATION_2_3
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

@Database(entities = [ShoppingCartTable::class , ShoppingCartItemsTable::class] , version = 3 , exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun newCartDao() : NewCartDao
    abstract fun shoppingCartItemsDao() : ShoppingCartItemsDao

    companion object {
        @Volatile
        private var INSTANCE : AppDatabase? = null

        @Synchronized
        fun getInstance(context : Context) : AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext , AppDatabase::class.java , "shopping_cart_db"
                ).addMigrations(migrations = arrayOf(MIGRATION_2_3)).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}