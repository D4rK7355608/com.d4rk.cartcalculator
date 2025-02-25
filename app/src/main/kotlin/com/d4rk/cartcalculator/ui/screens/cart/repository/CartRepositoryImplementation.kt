package com.d4rk.cartcalculator.ui.screens.cart.repository

import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import net.jpountz.lz4.LZ4Compressor
import net.jpountz.lz4.LZ4Factory
import org.msgpack.core.MessagePack
import org.msgpack.core.MessagePacker
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.net.URLEncoder
import java.util.Locale

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

    suspend fun generateCartShareLinkImplementation(cartIdentifier : Int) : String? {
        return runCatching {
            val shoppingCart : ShoppingCartTable? = loadCartIdImplementation(cartIdentifier)
            val cartItems : List<ShoppingCartItemsTable> = fetchItemsForCartImplementation(cartIdentifier)

            if (shoppingCart == null) {
                println("🚨 ShoppingCart is null for cartId: $cartIdentifier")
                return@runCatching null
            }
            if (cartItems.isEmpty()) {
                println("⚠️ No items found for cartId: $cartIdentifier")
            }

            println("🛒 Cart: $shoppingCart")
            println("📦 Items: $cartItems")

            shoppingCart.let {
                val serializedCart : ByteArray = serializeToMessagePack(shoppingCart , cartItems)
                println("🔍 Serialized Cart Size: ${serializedCart.size} bytes")

                val compressedCartData : ByteArray = compressLZ4(serializedCart)
                println("📦 Compressed Cart Size: ${compressedCartData.size} bytes")

                if (compressedCartData.isEmpty()) {
                    println("🚨 Compressed data is empty!")
                    return@runCatching null
                }

                val encodedData : String = encodeBase62(compressedCartData)
                if (encodedData.isEmpty()) {
                    println("🚨 Encoded data is empty!")
                    return@runCatching null
                }

                val urlEncodedCartData : String = URLEncoder.encode(encodedData , "UTF-8")
                println("✅ Final Encoded Data: $urlEncodedCartData")
                "https://cartcalculator.com/import?d=$urlEncodedCartData"
            }
        }.getOrElse {
            println("❌ Error in generating share link: ${it.message}")
            null
        }
    }

    private fun serializeToMessagePack(shoppingCart : ShoppingCartTable , cartItems : List<ShoppingCartItemsTable>) : ByteArray {
        val outputStream = ByteArrayOutputStream()
        val messagePacker : MessagePacker = MessagePack.newDefaultPacker(outputStream)

        messagePacker.packArrayHeader(3)
        messagePacker.packInt(shoppingCart.cartId)
        messagePacker.packString(shoppingCart.name)
        messagePacker.packLong(shoppingCart.date)

        messagePacker.packArrayHeader(cartItems.size)
        for (cartItem in cartItems) {
            messagePacker.packArrayHeader(4)
            messagePacker.packInt(cartItem.itemId)
            messagePacker.packString(cartItem.name)
            messagePacker.packInt(cartItem.quantity)
            messagePacker.packFloat(cartItem.price.toFloat())
        }

        messagePacker.close()
        val data = outputStream.toByteArray()
        println("✅ MessagePack Data Size: ${data.size} bytes")
        return data
    }

    private fun compressLZ4(uncompressedData : ByteArray) : ByteArray {
        val factory : LZ4Factory = LZ4Factory.fastestInstance()
        val compressor : LZ4Compressor = factory.fastCompressor()
        val maxCompressedLength : Int = compressor.maxCompressedLength(uncompressedData.size)
        val compressedData = ByteArray(maxCompressedLength)
        val actualCompressedSize : Int = compressor.compress(uncompressedData , 0 , uncompressedData.size , compressedData , 0 , maxCompressedLength)

        val finalData = ByteArray(size = 4 + actualCompressedSize)
        finalData[0] = (uncompressedData.size shr 24).toByte()
        finalData[1] = (uncompressedData.size shr 16).toByte()
        finalData[2] = (uncompressedData.size shr 8).toByte()
        finalData[3] = (uncompressedData.size).toByte()
        System.arraycopy(compressedData , 0 , finalData , 4 , actualCompressedSize)
        return finalData
    }

    private fun encodeBase62(input : ByteArray) : String {
        if (input.isEmpty()) return ""
        val characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        var numericValue = BigInteger(1 , input)
        val encoded : StringBuilder = StringBuilder()
        while (numericValue > BigInteger.ZERO) {
            val index : BigInteger = numericValue.mod(BigInteger.valueOf(62))
            encoded.insert(0 , characters[index.toInt()])
            numericValue = numericValue.divide(BigInteger.valueOf(62))
        }
        val dataSizePrefix : String = String.format(Locale.getDefault(), "%04d" , input.size)
        return dataSizePrefix + encoded.toString()
    }
}