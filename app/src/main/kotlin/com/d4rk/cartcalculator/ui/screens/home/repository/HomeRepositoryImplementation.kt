package com.d4rk.cartcalculator.ui.screens.home.repository

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.ui.screens.cart.CartActivity
import net.jpountz.lz4.LZ4Factory
import net.jpountz.lz4.LZ4FastDecompressor
import org.msgpack.core.MessagePack
import org.msgpack.core.MessageUnpacker
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.net.URLDecoder

abstract class HomeRepositoryImplementation(val application : Application) {

    suspend fun importSharedCartImplementation(encodedData : String) {
        runCatching {
            println("🔍 Decoding shared cart data...")

            val uri : Uri = Uri.parse(encodedData)
            val base62EncodedData : String = uri.getQueryParameter("d") ?: ""

            if (base62EncodedData.isEmpty()) {
                println("🚨 Error: No `d` parameter found in the URL!")
                return@runCatching
            }

            val urlDecodedData : String = URLDecoder.decode(base62EncodedData , "UTF-8")
            println("✅ Extracted Base62 Data: $urlDecodedData")

            val compressedBytes : ByteArray = decodeBase62(encoded = urlDecodedData)
            if (compressedBytes.isEmpty()) {
                println("🚨 Decoding failed: Empty compressed data")
                return@runCatching
            }

            val binaryData : ByteArray = decompressLZ4(compressedDataWithHeader = compressedBytes)
            if (binaryData.isEmpty()) {
                println("🚨 LZ4 decompression failed: Data is empty")
                return@runCatching
            }

            val (cart : ShoppingCartTable , items : List<ShoppingCartItemsTable>) = deserializeMessagePack(serializedData = binaryData)

            var newCartName : String = cart.name
            val allCarts : List<ShoppingCartTable> = AppCoreManager.database.newCartDao().getAll()
            var suffix = 1
            while (allCarts.any { it.name.equals(newCartName , ignoreCase = true) }) {
                newCartName = "${cart.name} ($suffix)"
                suffix ++
            }
            val newCart : ShoppingCartTable = cart.copy(cartId = 0 , name = newCartName)
            val newCartId : Long = AppCoreManager.database.newCartDao().insert(newCart)
            println("✅ Imported Cart: ${newCart.name} (ID: $newCartId)")

            items.forEach { item ->
                val newItem = item.copy(itemId = 0 , cartId = newCartId.toInt())
                AppCoreManager.database.shoppingCartItemsDao().insert(newItem)
            }

            println("✅ Imported ${items.size} items into cart.")
        }.onFailure { e ->
            println("❌ Error importing shared cart: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun decodeBase62(encoded : String) : ByteArray {
        if (encoded.length < 4) {
            println("🚨 Encoded string is too short!")
            return ByteArray(0)
        }
        val encodedLength : String = encoded.substring(startIndex = 0 , endIndex = 4)
        val decodedLength : Int = encodedLength.toIntOrNull() ?: 0
        val encodedPayload : String = encoded.substring(startIndex = 4)

        val base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        var decodedValue : BigInteger = BigInteger.ZERO
        val base : BigInteger = BigInteger.valueOf(62)
        for (char in encodedPayload) {
            val index : Int = base62Chars.indexOf(char)
            if (index == - 1) {
                println("🚨 Invalid Base62 character: $char")
                return ByteArray(0)
            }
            decodedValue = decodedValue.multiply(base).add(BigInteger.valueOf(index.toLong()))
        }

        val decodedBytes : ByteArray = decodedValue.toByteArray()
        val significantBytes : ByteArray = if (decodedBytes.isNotEmpty() && decodedBytes[0] == 0.toByte()) {
            decodedBytes.drop(n = 1).toByteArray()
        }
        else {
            decodedBytes
        }
        return if (significantBytes.size < decodedLength) {
            ByteArray(decodedLength - significantBytes.size).plus(significantBytes)
        }
        else {
            significantBytes
        }
    }

    private fun decompressLZ4(compressedDataWithHeader : ByteArray) : ByteArray {
        if (compressedDataWithHeader.size < 4) throw IllegalArgumentException("Invalid input for LZ4 decompression.")

        val originalUncompressedSize : Int = ((compressedDataWithHeader[0].toInt() and 0xFF) shl 24) or ((compressedDataWithHeader[1].toInt() and 0xFF) shl 16) or ((compressedDataWithHeader[2].toInt() and 0xFF) shl 8) or (compressedDataWithHeader[3].toInt() and 0xFF)

        val actualCompressedData : ByteArray = compressedDataWithHeader.copyOfRange(fromIndex = 4 , toIndex = compressedDataWithHeader.size)

        val factory : LZ4Factory = LZ4Factory.fastestInstance()
        val decompressor : LZ4FastDecompressor = factory.fastDecompressor()

        val decompressed = ByteArray(originalUncompressedSize)
        decompressor.decompress(actualCompressedData , 0 , decompressed , 0 , originalUncompressedSize)
        println("✅ Decompressed Size: $originalUncompressedSize")
        return decompressed
    }

    private fun deserializeMessagePack(serializedData : ByteArray) : Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> {
        val messagePackUnpacker : MessageUnpacker = MessagePack.newDefaultUnpacker(ByteArrayInputStream(serializedData))

        val cartDataCount : Int = messagePackUnpacker.unpackArrayHeader()
        if (cartDataCount != 3) {
            println("🚨 Expected cart array header of size 3, got: $cartDataCount")
            throw IllegalArgumentException("Invalid cart data format")
        }
        val cartId : Int = messagePackUnpacker.unpackInt()
        val cartName : String = messagePackUnpacker.unpackString()
        val shoppingCartCreationTimestamp : Long = messagePackUnpacker.unpackLong()
        val cart = ShoppingCartTable(cartId = cartId , name = cartName , date = shoppingCartCreationTimestamp)

        val itemsCount : Int = messagePackUnpacker.unpackArrayHeader()
        val shoppingCartItems : MutableList<ShoppingCartItemsTable> = mutableListOf()

        repeat(times = itemsCount) {
            val itemDataCount : Int = messagePackUnpacker.unpackArrayHeader()
            if (itemDataCount != 4) {
                println("🚨 Expected item array header of size 4, got: $itemDataCount")
                throw IllegalArgumentException("Invalid item data format")
            }

            val itemId : Int = messagePackUnpacker.unpackInt()
            val itemName : String = messagePackUnpacker.unpackString()
            val itemQuantity : Int = messagePackUnpacker.unpackInt()
            val itemPrice : String = messagePackUnpacker.unpackFloat().toString()

            shoppingCartItems.add(
                ShoppingCartItemsTable(
                    itemId = itemId , cartId = cartId , name = itemName , price = itemPrice , quantity = itemQuantity
                )
            )
        }

        messagePackUnpacker.close()
        println("✅ Deserialized Cart: ${cart.name} (ID: ${cart.cartId}), Items: ${shoppingCartItems.size}")
        return Pair(first = cart , second = shoppingCartItems)
    }

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