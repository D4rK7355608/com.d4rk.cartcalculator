package com.d4rk.cartcalculator.app.home.domain.usecases

import android.net.Uri
import androidx.core.net.toUri
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import net.jpountz.lz4.LZ4Factory
import org.msgpack.core.MessagePack
import org.msgpack.core.MessageUnpacker
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.net.URLDecoder

class DecryptSharedCartUseCase : Repository<String , Flow<DataState<Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> , Errors>>> {

    override suspend fun invoke(param : String) : Flow<DataState<Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> , Errors>> = flow {
        runCatching {
            val uri : Uri = param.toUri()
            val base62EncodedData : String = uri.getQueryParameter("d") ?: throw IllegalArgumentException("No 'd' parameter found in URL")

            val urlDecodedData : String = withContext(context = Dispatchers.IO) { URLDecoder.decode(base62EncodedData , "UTF-8") }
            val compressedBytes : ByteArray = decodeBase62(encoded = urlDecodedData)
            val binaryData : ByteArray = decompressLZ4(compressedData = compressedBytes)

            deserializeMessagePack(serializedData = binaryData)
        }.onSuccess { result ->
            emit(value = DataState.Success(data = result))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_DECRYPT_CART)))
        }
    }

    private fun decodeBase62(encoded : String) : ByteArray {
        if (encoded.length < 4) throw IllegalArgumentException("Encoded string is too short!")

        val encodedLength : String = encoded.substring(startIndex = 0 , endIndex = 4)
        val decodedLength : Int = encodedLength.toIntOrNull() ?: throw IllegalArgumentException("Invalid length header in encoded string")
        val encodedPayload : String = encoded.substring(startIndex = 4)

        val base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        var decodedValue : BigInteger = BigInteger.ZERO
        val base : BigInteger = BigInteger.valueOf(62)
        for (char in encodedPayload) {
            val index : Int = base62Chars.indexOf(char = char)
            if (index == - 1) throw IllegalArgumentException("Invalid Base62 character: $char")
            decodedValue = decodedValue.multiply(base).add(BigInteger.valueOf(index.toLong()))
        }

        val decodedBytes = decodedValue.toByteArray()
        val significantBytes = if (decodedBytes.isNotEmpty() && decodedBytes[0] == 0.toByte()) {
            decodedBytes.drop(1).toByteArray()
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

    private fun decompressLZ4(compressedData : ByteArray) : ByteArray {
        if (compressedData.size < 4) throw IllegalArgumentException("Invalid input for LZ4 decompression.")

        val originalSize : Int = ((compressedData[0].toInt() and 0xFF) shl 24) or ((compressedData[1].toInt() and 0xFF) shl 16) or ((compressedData[2].toInt() and 0xFF) shl 8) or (compressedData[3].toInt() and 0xFF)

        val compressedContent : ByteArray = compressedData.copyOfRange(fromIndex = 4 , toIndex = compressedData.size)

        val factory = LZ4Factory.fastestInstance()
        val decompressor = factory.fastDecompressor()
        val decompressed = ByteArray(originalSize)

        decompressor.decompress(compressedContent , 0 , decompressed , 0 , originalSize)
        return decompressed
    }

    private fun deserializeMessagePack(serializedData : ByteArray) : Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> {
        val unpacker : MessageUnpacker = MessagePack.newDefaultUnpacker(ByteArrayInputStream(serializedData))

        val cartDataCount : Int = unpacker.unpackArrayHeader()
        if (cartDataCount != 3) {
            throw IllegalArgumentException("Invalid cart data format: expected 3 elements, got $cartDataCount")
        }

        val cartId : Int = unpacker.unpackInt()
        val cartName : String = unpacker.unpackString()
        val cartDate : Long = unpacker.unpackLong()
        val cart = ShoppingCartTable(cartId = cartId , name = cartName , date = cartDate)

        val itemsCount : Int = unpacker.unpackArrayHeader()
        val items : MutableList<ShoppingCartItemsTable> = mutableListOf()
        repeat(itemsCount) {
            val itemDataCount : Int = unpacker.unpackArrayHeader()
            if (itemDataCount != 4) {
                throw IllegalArgumentException("Invalid item data format: expected 4 elements, got $itemDataCount")
            }

            val itemId : Int = unpacker.unpackInt()
            val itemName : String = unpacker.unpackString()
            val itemQuantity : Int = unpacker.unpackInt()
            val itemPrice : String = unpacker.unpackFloat().toString()
            items.add(ShoppingCartItemsTable(itemId , cartId , itemName , itemPrice , itemQuantity))
        }

        unpacker.close()
        return Pair(first = cart , second = items)
    }
}