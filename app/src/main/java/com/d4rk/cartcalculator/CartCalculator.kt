package com.d4rk.cartcalculator

import com.d4rk.android.libs.apptoolkit.data.core.BaseCoreManager
import com.d4rk.cartcalculator.core.di.initializeKoin

class CartCalculator: BaseCoreManager() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin(context = this@CartCalculator)
    }
}