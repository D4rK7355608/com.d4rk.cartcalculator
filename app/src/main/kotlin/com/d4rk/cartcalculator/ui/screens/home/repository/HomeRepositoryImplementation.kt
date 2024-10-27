package com.d4rk.cartcalculator.ui.screens.home.repository

import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable

abstract class HomeRepositoryImplementation {

    suspend fun getCarts() : List<ShoppingCartTable> {
        return AppCoreManager.database.newCartDao().getAll()
    }

    suspend fun addCartToDatabase(cart : ShoppingCartTable) {
        AppCoreManager.database.newCartDao().insert(cart)
    }

    suspend fun deleteCartFromDatabase(cart : ShoppingCartTable) {
        with(AppCoreManager.database) {
            newCartDao().delete(cart)
            shoppingCartItemsDao().deleteItemsFromCart(cart.cartId)
        }
    }
}