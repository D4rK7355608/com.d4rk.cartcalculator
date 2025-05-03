package com.d4rk.cartcalculator.core.di.modules

import androidx.room.Room
import com.d4rk.cartcalculator.core.data.database.AppDatabase
import com.d4rk.cartcalculator.core.data.database.DataBaseImplementation
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.dao.NewCartDao
import com.d4rk.cartcalculator.core.data.database.dao.ShoppingCartItemsDao
import com.d4rk.cartcalculator.core.data.database.migrations.MIGRATION_2_3
import org.koin.core.module.Module
import org.koin.dsl.module

val dataBaseModule : Module = module {
    single<AppDatabase> {
        Room.databaseBuilder(context = get() , klass = AppDatabase::class.java , name = "shopping_cart_db").addMigrations(MIGRATION_2_3).fallbackToDestructiveMigration(dropAllTables = true).fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true).build()
    }

    single<NewCartDao> { get<AppDatabase>().newCartDao() }
    single<ShoppingCartItemsDao> { get<AppDatabase>().shoppingCartItemsDao() }

    single<DatabaseInterface> { DataBaseImplementation(database = get()) }
}