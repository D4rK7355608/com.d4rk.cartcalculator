package com.d4rk.cartcalculator.core.utils.extensions

fun String.isValidCartLink(): Boolean {
    val regex = Regex(pattern = "^https://cartcalculator\\.com/import\\?d=[A-Za-z0-9]+$")
    return this.matches(regex = regex)
}