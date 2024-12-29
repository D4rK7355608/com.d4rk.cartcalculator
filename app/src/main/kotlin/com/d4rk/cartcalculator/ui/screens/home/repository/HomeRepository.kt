package com.d4rk.cartcalculator.ui.screens.home.repository

import android.app.Application
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(application : Application) :
    HomeRepositoryImplementation(application = application) {

    suspend fun loadCartsRepository(onSuccess : (List<ShoppingCartTable>) -> Unit) {
        withContext(Dispatchers.IO) {
            val carts : List<ShoppingCartTable> = loadCartsImplementation()
            withContext(Dispatchers.Main) {
                onSuccess(carts)
            }
        }
    }

    suspend fun openCartRepository(cart : ShoppingCartTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            openCartImplementation(cart = cart)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun addCartRepository(cart : ShoppingCartTable , onSuccess : (ShoppingCartTable) -> Unit) {
        withContext(Dispatchers.IO) {
            val addedCart : ShoppingCartTable = addCartImplementation(cart = cart)
            withContext(Dispatchers.Main) {
                onSuccess(addedCart)
            }
        }
    }

    suspend fun deleteCartRepository(cart : ShoppingCartTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            deleteCartImplementation(cart = cart)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}