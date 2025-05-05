package com.d4rk.cartcalculator.core.data.database

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

class DataBaseImplementation(private val database : AppDatabase) : DatabaseInterface {

    override suspend fun insertCart(cart : ShoppingCartTable) : Long {
        return database.newCartDao().insert(cart)
    }

    override suspend fun deleteCart(cart : ShoppingCartTable) {
        database.newCartDao().delete(cart)
    }

    override suspend fun updateCart(cart : ShoppingCartTable) {
        database.newCartDao().updateCart(cart)
    }

    override suspend fun getAllCarts() : List<ShoppingCartTable> {
        return database.newCartDao().getAll()
    }

    override suspend fun getCartById(cartId : Int) : ShoppingCartTable? {
        return database.newCartDao().getCartById(cartId)
    }

    override suspend fun insertItem(item : ShoppingCartItemsTable) : Long {
        return database.shoppingCartItemsDao().insert(item)
    }

    override suspend fun updateItem(item : ShoppingCartItemsTable) {
        database.shoppingCartItemsDao().update(item)
    }

    override suspend fun deleteItem(item : ShoppingCartItemsTable) {
        database.shoppingCartItemsDao().delete(item)
    }

    override suspend fun getItemsByCartId(cartId : Int) : List<ShoppingCartItemsTable> {
        return database.shoppingCartItemsDao().getItemsByCartId(cartId)
    }

    override suspend fun deleteItemsFromCart(cartId : Int) {
        database.shoppingCartItemsDao().deleteItemsFromCart(cartId)
    }
}