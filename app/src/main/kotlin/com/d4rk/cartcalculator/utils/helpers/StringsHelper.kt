package com.d4rk.cartcalculator.utils.helpers

import androidx.annotation.StringRes
import com.d4rk.cartcalculator.data.core.AppCoreManager

fun getStringResource(@StringRes id: Int): String {
    return AppCoreManager.instance.getString(id)
}