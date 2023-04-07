package com.d4rk.cartcalculator.data
data class CartItem(val name: String, val unitPrice: Double, var quantity: Int) {
    fun totalPrice(): Double {
        return unitPrice * quantity
    }
}