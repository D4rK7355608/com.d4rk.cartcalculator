package com.d4rk.cartcalculator

import android.app.Application
import androidx.room.Room
import com.d4rk.cartcalculator.data.db.AppDatabase

class MyApp : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "my_database").build()
    }
}