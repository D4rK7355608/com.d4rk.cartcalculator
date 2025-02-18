package com.d4rk.cartcalculator.ui.screens.cart.repository

import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CartRepository : CartRepositoryImplementation() {

    suspend fun loadCartItemsRepository(cartId: Int , onSuccess: (List<ShoppingCartItemsTable>) -> Unit) {
        withContext(Dispatchers.IO) {
            val items : List<ShoppingCartItemsTable> = fetchItemsForCartImplementation(cartId = cartId)
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
            val newItemId : Int = addCartItemImplementation(cartItem = cartItem).toInt()
            cartItem.itemId = newItemId
            withContext(Dispatchers.Main) {
                onSuccess(cartItem)
            }
        }
    }

    suspend fun updateCartItemRepository(cartItem : ShoppingCartItemsTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            modifyCartItemImplementation(cartItem = cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun deleteCartItemRepository(cartItem : ShoppingCartItemsTable , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            removeCartItemImplementation(cartItem = cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun saveCartItemsRepository(cartItem: ShoppingCartItemsTable , onSuccess: () -> Unit) {
        withContext(Dispatchers.IO) {
            saveCartItemsImplementation(cartItems = cartItem)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun generateCartShareLinkRepository(cartId: Int, onSuccess: (String?) -> Unit) {
        withContext(Dispatchers.IO) {
            val shareLink: String? = generateCartShareLinkImplementation(cartId)
            withContext(Dispatchers.Main) { onSuccess(shareLink) }
        }
    }
}