package com.d4rk.cartcalculator.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 : Migration = object : Migration(startVersion = 2 , endVersion = 3) {
    override fun migrate(db : SupportSQLiteDatabase) {
        db.execSQL(sql = "ALTER TABLE ShoppingCartItemsTable ADD COLUMN isChecked INTEGER NOT NULL DEFAULT 0")
    }
}