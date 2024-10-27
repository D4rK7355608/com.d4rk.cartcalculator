package com.d4rk.cartcalculator.ui.screens.cart.repository

import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository : CartRepositoryImplementation() {

    suspend fun getCartItems(cartId: Int, onSuccess: (List<ShoppingCartItemsTable>) -> Unit) {
        withContext(Dispatchers.IO) {
            val items = getItemsByCartId(cartId)
            println("Shopping Cart Calculator -> [CartRepository] getCartItems called for cartId: $cartId, Items retrieved: ${items}")
            withContext(Dispatchers.Main) {
                onSuccess(items)
            }
        }
    }

    suspend fun addItemToCart(
        cartItem : ShoppingCartItemsTable ,
        onSuccess : (ShoppingCartItemsTable) -> Unit ,
    ) {
        withContext(Dispatchers.IO) {
            val newItemId = insertCartItem(cartItem).toInt()
            cartItem.itemId = newItemId
            withContext(Dispatchers.Main) {
                onSuccess(cartItem)
            }
        }
    }

    suspend fun updateCartItem(cartItem : ShoppingCartItemsTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            updateCartItem(cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun deleteCartItem(cartItem : ShoppingCartItemsTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            deleteCartItem(cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun saveCartItems(cartItem: ShoppingCartItemsTable, onSuccess: () -> Unit) {
        withContext(Dispatchers.IO) {
            saveCartItemsInDatabase(cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}