package com.d4rk.cartcalculator.core.domain.usecases.cart

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.jpountz.lz4.LZ4Compressor
import net.jpountz.lz4.LZ4Factory
import org.msgpack.core.MessagePack
import org.msgpack.core.MessagePacker
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.net.URLEncoder

class GenerateCartShareLinkUseCase(private val database : DatabaseInterface) : Repository<Int , Flow<DataState<String , Errors>>> {

    override suspend fun invoke(param : Int) : Flow<DataState<String , Errors>> = flow {
        emit(DataState.Loading())
        runCatching {
            database.getCartById(cartId = param)?.let { cart ->
                val cartItems = database.getItemsByCartId(cartId = param)

                if (cartItems.isEmpty()) {
                    throw IllegalStateException(Errors.UseCase.EMPTY_CART.toString())
                }

                val serializedCart : ByteArray = serializeToMessagePack(cart , cartItems)
                val compressedCartData : ByteArray = compressLZ4(serializedCart)

                if (compressedCartData.isEmpty()) {
                    throw IllegalStateException(Errors.UseCase.COMPRESSION_FAILED.toString())
                }

                val encodedData : String = encodeBase62(compressedCartData)
                val urlEncodedCartData : String = URLEncoder.encode(encodedData , "UTF-8")

                "https://cartcalculator.com/import?d=$urlEncodedCartData"
            } ?: throw IllegalStateException(Errors.UseCase.CART_NOT_FOUND.toString())
        }.onSuccess { url ->
            emit(DataState.Success(url))
        }.onFailure { throwable ->
            emit(DataState.Error(error = throwable.toError()))
        }
    }

    private fun serializeToMessagePack(cart : ShoppingCartTable , cartItems : List<ShoppingCartItemsTable>) : ByteArray {
        val outputStream = ByteArrayOutputStream()
        val packer : MessagePacker = MessagePack.newDefaultPacker(outputStream)

        packer.packInt(cart.cartId)
        packer.packString(cart.name)
        packer.packLong(cart.date)

        packer.packArrayHeader(cartItems.size)
        for (item in cartItems) {
            packer.packInt(item.itemId)
            packer.packString(item.name)
            packer.packInt(item.quantity)
            packer.packFloat(item.price.toFloat())
        }

        packer.close()
        return outputStream.toByteArray()
    }

    private fun compressLZ4(data : ByteArray) : ByteArray {
        val factory : LZ4Factory = LZ4Factory.fastestInstance()
        val compressor : LZ4Compressor = factory.fastCompressor()
        val maxCompressedLength : Int = compressor.maxCompressedLength(data.size)
        val compressedData = ByteArray(maxCompressedLength)

        val compressedSize : Int = compressor.compress(data , 0 , data.size , compressedData , 0 , maxCompressedLength)

        return ByteArray(4 + compressedSize).apply {
            this[0] = (data.size shr 24).toByte()
            this[1] = (data.size shr 16).toByte()
            this[2] = (data.size shr 8).toByte()
            this[3] = data.size.toByte()
            System.arraycopy(compressedData , 0 , this , 4 , compressedSize)
        }
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

        return encoded.toString()
    }
}