package com.d4rk.cartcalculator.ui.screens.home.repository

import android.app.Application
import android.content.Intent
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.ui.screens.cart.CartActivity

abstract class HomeRepositoryImplementation(val application : Application) {

    suspend fun loadCartsImplementation() : List<ShoppingCartTable> {
        return AppCoreManager.database.newCartDao().getAll()
    }

    fun openCartImplementation(cart : ShoppingCartTable) {
        println("Shopping Cart Calculator -> HomeRepositoryImplementation: Opening cart with ID: ${cart.cartId}")

        application.startActivity(
            Intent(application , CartActivity::class.java).putExtra("cartId" , cart.cartId)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    suspend fun addCartImplementation(cart : ShoppingCartTable) {
        AppCoreManager.database.newCartDao().insert(cart)
    }

    suspend fun deleteCartImplementation(cart : ShoppingCartTable) {
        with(AppCoreManager.database) {
            newCartDao().delete(cart)
            shoppingCartItemsDao().deleteItemsFromCart(cart.cartId)
        }
    }
}