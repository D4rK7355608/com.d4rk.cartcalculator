package com.d4rk.cartcalculator.ui.screens.home.repository

import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository : HomeRepositoryImplementation() {

    suspend fun loadCarts(onSuccess : (List<ShoppingCartTable>) -> Unit) {
        withContext(Dispatchers.IO) {
            val carts = getCarts()
            withContext(Dispatchers.Main) {
                onSuccess(carts)
            }
        }
    }

    suspend fun addCart(cart : ShoppingCartTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            addCartToDatabase(cart)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun deleteCart(cart : ShoppingCartTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            deleteCartFromDatabase(cart)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}