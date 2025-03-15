package com.d4rk.cartcalculator.core.data.database

import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

interface DatabaseInterface {

    // ShoppingCartTable Methods
    suspend fun insertCart(cart : ShoppingCartTable) : Long
    suspend fun deleteCart(cart : ShoppingCartTable)
    suspend fun getAllCarts() : List<ShoppingCartTable>
    suspend fun getCartById(cartId : Int) : ShoppingCartTable?

    // ShoppingCartItemsTable Methods
    suspend fun insertItem(item : ShoppingCartItemsTable) : Long
    suspend fun updateItem(item : ShoppingCartItemsTable)
    suspend fun deleteItem(item : ShoppingCartItemsTable)
    suspend fun getItemsByCartId(cartId : Int) : List<ShoppingCartItemsTable>
    suspend fun deleteItemsFromCart(cartId : Int)
}
