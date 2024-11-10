package com.d4rk.cartcalculator.ui.screens.cart.repository

import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository : CartRepositoryImplementation() {

    suspend fun loadCartItemsRepository(cartId: Int , onSuccess: (List<ShoppingCartItemsTable>) -> Unit) {
        withContext(Dispatchers.IO) {
            val items = fetchItemsForCartImplementation(cartId)
            withContext(Dispatchers.Main) {
                onSuccess(items)
            }
        }
    }

    suspend fun addCartItemRepository(
        cartItem : ShoppingCartItemsTable ,
        onSuccess : (ShoppingCartItemsTable) -> Unit ,
    ) {
        withContext(Dispatchers.IO) {
            val newItemId = addCartItemImplementation(cartItem).toInt()
            cartItem.itemId = newItemId
            withContext(Dispatchers.Main) {
                onSuccess(cartItem)
            }
        }
    }

    suspend fun updateCartItemRepository(cartItem : ShoppingCartItemsTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            modifyCartItemImplementation(cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun deleteCartItemRepository(cartItem : ShoppingCartItemsTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            removeCartItemImplementation(cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun saveCartItemsRepository(cartItem: ShoppingCartItemsTable , onSuccess: () -> Unit) {
        withContext(Dispatchers.IO) {
            saveCartItemsImplementation(cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}