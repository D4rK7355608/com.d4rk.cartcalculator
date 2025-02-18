package com.d4rk.cartcalculator.ui.screens.cart.repository

import android.util.Base64
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

abstract class CartRepositoryImplementation {

    suspend fun loadCartIdImplementation(cartId : Int) : ShoppingCartTable? = AppCoreManager.database.newCartDao().getCartById(cartId = cartId)

    suspend fun fetchItemsForCartImplementation(cartId : Int) : List<ShoppingCartItemsTable> = AppCoreManager.database.shoppingCartItemsDao().getItemsByCartId(cartId = cartId)

    suspend fun addCartItemImplementation(cartItem : ShoppingCartItemsTable) : Long = AppCoreManager.database.shoppingCartItemsDao().insert(item = cartItem)

    suspend fun modifyCartItemImplementation(cartItem : ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().update(item = cartItem)
    }

    suspend fun removeCartItemImplementation(cartItem : ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().delete(item = cartItem)
    }

    suspend fun saveCartItemsImplementation(cartItems : ShoppingCartItemsTable) {
        AppCoreManager.database.shoppingCartItemsDao().update(item = cartItems)
    }

    suspend fun generateCartShareLinkImplementation(cartId : Int) : String? {
        return runCatching {
            val cart : ShoppingCartTable? = loadCartIdImplementation(cartId)
            val items : List<ShoppingCartItemsTable> = fetchItemsForCartImplementation(cartId)
            cart?.let {
                val cartData : Map<String , String> = mapOf(
                    "cart" to Json.encodeToString(it) , "items" to Json.encodeToString(items)
                )
                val encodedData : String = encodeBase64UrlSafe(compressJson(Json.encodeToString(cartData)))
                println("Encoded Data: $encodedData")
                "https://cartcalculator.com/import?data=$encodedData"
            }
        }.getOrElse {
            null
        }
    }

    private fun encodeBase64UrlSafe(input : ByteArray) : String {
        return Base64.encodeToString(input , Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun compressJson(json : String) : ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).use { it.write(json.toByteArray()) }
        return byteArrayOutputStream.toByteArray()
    }
}