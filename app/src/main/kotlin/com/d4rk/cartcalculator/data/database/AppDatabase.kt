package com.d4rk.cartcalculator.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.d4rk.cartcalculator.data.database.dao.NewCartDao
import com.d4rk.cartcalculator.data.database.dao.ShoppingCartItemsDao
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.repository.DateConverter

@Database(entities = [ShoppingCartTable::class, ShoppingCartItemsTable::class], version = 3, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newCartDao(): NewCartDao
    abstract fun shoppingCartItemsDao(): ShoppingCartItemsDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2 , 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ShoppingCartItemsTable ADD COLUMN isChecked INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}