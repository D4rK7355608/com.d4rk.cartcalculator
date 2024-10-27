package com.d4rk.cartcalculator.ui.screens.cart.repository

import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable

abstract class CartRepositoryImplementation {

    suspend fun getCartById(cartId : Int) : ShoppingCartTable {
        return AppCoreManager.database.newCartDao().getCartById(cartId)
    }

    suspend fun getItemsByCartId(cartId : Int) : List<ShoppingCartItemsTable> {
        return AppCoreManager.database.shoppingCartItemsDao().getItemsByCartId(cartId)
    }

    suspend fun insertCartItem(cartItem : ShoppingCartItemsTable) : Long {
        return AppCoreManager.database.shoppingCartItemsDao().insert(cartItem)
    }

    suspend fun updateCartItem(cartItem : ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().update(cartItem)
    }

    suspend fun deleteCartItem(cartItem : ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().delete(cartItem)
    }

    suspend fun saveCartItemsInDatabase(cartItems: ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().update(cartItems)
    }
}
