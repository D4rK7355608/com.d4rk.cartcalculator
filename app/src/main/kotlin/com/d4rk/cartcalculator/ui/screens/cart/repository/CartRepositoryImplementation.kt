package com.d4rk.cartcalculator.ui.screens.cart.repository

import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable

abstract class CartRepositoryImplementation {

    suspend fun fetchCartByIdImplementation(cartId : Int) : ShoppingCartTable {
        return AppCoreManager.database.newCartDao().getCartById(cartId)
    }

    suspend fun fetchItemsForCartImplementation(cartId : Int) : List<ShoppingCartItemsTable> {
        return AppCoreManager.database.shoppingCartItemsDao().getItemsByCartId(cartId)
    }

    suspend fun addCartItemImplementation(cartItem : ShoppingCartItemsTable) : Long {
        return AppCoreManager.database.shoppingCartItemsDao().insert(cartItem)
    }

    suspend fun modifyCartItemImplementation(cartItem : ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().update(cartItem)
    }

    suspend fun removeCartItemImplementation(cartItem : ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().delete(cartItem)
    }

    suspend fun saveCartItemsImplementation(cartItems: ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().update(cartItems)
    }
}
