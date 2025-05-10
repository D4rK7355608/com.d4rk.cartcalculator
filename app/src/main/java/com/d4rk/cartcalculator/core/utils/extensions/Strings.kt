package com.d4rk.cartcalculator.core.utils.extensions

fun String.isValidShareLink(): Boolean {
    return startsWith("https://cartcalculator/i?d=")
}