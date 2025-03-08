package com.d4rk.cartcalculator

import android.app.Application
import com.d4rk.cartcalculator.core.di.initializeKoin

class CartCalculator: Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin(context = this@CartCalculator)
    }
}