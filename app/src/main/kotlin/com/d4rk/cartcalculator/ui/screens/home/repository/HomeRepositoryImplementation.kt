package com.d4rk.cartcalculator.ui.screens.home.repository

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.ui.screens.cart.CartActivity
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.net.URLDecoder
import java.util.zip.GZIPInputStream

abstract class HomeRepositoryImplementation(val application : Application) {

    suspend fun importSharedCartImplementation(encodedData : String) {
        runCatching {
            decodeBase64UrlSafe(encodedData)?.let { compressedBytes ->
                val json : String = decompressJson(compressedBytes)
                val data : Map<String , String> = Json.decodeFromString(json)

                data["cart"]?.let { cartJson ->
                    data["items"]?.let { itemsJson ->
                        val cart : ShoppingCartTable = Json.decodeFromString(cartJson)
                        val items : List<ShoppingCartItemsTable> = Json.decodeFromString(itemsJson)

                        AppCoreManager.database.newCartDao().getCartById(cart.cartId)?.let {} ?: run {
                            val newCartId : Long = AppCoreManager.database.newCartDao().insert(cart)
                            items.forEach { item ->
                                AppCoreManager.database.shoppingCartItemsDao().insert(item.copy(cartId = newCartId.toInt()))
                            }
                        }
                    }
                }
            }
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    private fun decodeBase64UrlSafe(pastedUrl : String) : ByteArray? {
        val rawData : String = Uri.parse(pastedUrl).getQueryParameter("data") ?: return null
        val decodedString : String = URLDecoder.decode(rawData , "UTF-8")
        return Base64.decode(decodedString , Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun decompressJson(compressedBytes : ByteArray) : String = GZIPInputStream(ByteArrayInputStream(compressedBytes)).bufferedReader().use { it.readText() }

    suspend fun loadCartsImplementation() : List<ShoppingCartTable> = AppCoreManager.database.newCartDao().getAll()

    fun openCartImplementation(cart : ShoppingCartTable) {
        application.startActivity(Intent(application , CartActivity::class.java).apply {
            putExtra("cartId" , cart.cartId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    suspend fun addCartImplementation(cart : ShoppingCartTable) : ShoppingCartTable = cart.copy(cartId = AppCoreManager.database.newCartDao().insert(cart = cart).toInt())

    suspend fun deleteCartImplementation(cart : ShoppingCartTable) {
        with(AppCoreManager.database) {
            newCartDao().delete(cart = cart)
            shoppingCartItemsDao().deleteItemsFromCart(cartId = cart.cartId)
        }
    }
}