package com.d4rk.cartcalculator.core.domain.usecases.cart

import android.os.Build
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.GZIPOutputStream
import kotlin.io.encoding.ExperimentalEncodingApi

class GenerateCartShareLinkUseCase(private val database : DatabaseInterface) : Repository<Int , Flow<DataState<String , Errors>>> {

    @OptIn(ExperimentalSerializationApi::class , ExperimentalEncodingApi::class)
    override suspend fun invoke(param : Int) : Flow<DataState<String , Errors>> = flow {
        runCatching {
            val cart : ShoppingCartTable = database.getCartById(cartId = param) ?: error("Cart not found")
            val details : List<ShoppingCartItemsTable> = database.getItemsByCartId(cartId = param)
            if (details.isEmpty()) error(message = "Empty details")

            val rawString : String = flatten(event = cart , item = details.first())
            val compressed : ByteArray = compressGzip(input = rawString.encodeToByteArray())
            val encoded : String = encodeBase64UrlSafe(data = compressed)

            "https://cartcalculator/i?d=$encoded"
        }.onSuccess {
            emit(value = DataState.Success(data = it))
        }.onFailure {
            emit(value = DataState.Error(error = it.toError()))
        }
    }

    private fun flatten(event : ShoppingCartTable , item : ShoppingCartItemsTable) : String {
        val values : List<String> = listOf(
            "id=${event.cartId}" ,
            "name=${event.name}" ,
            "date=${event.date}" ,
            "shared=${true}" ,
            "iid=${item.itemId}" ,
            "iname=${item.name}" ,
            "quantity=${item.quantity}" ,
            "price=${item.price}",
            "checked=${item.isChecked}"
        )
        return values.joinToString(separator = ";")
    }

    private fun compressGzip(input : ByteArray) : ByteArray {
        val output = ByteArrayOutputStream()
        GZIPOutputStream(output).use { it.write(input) }
        return output.toByteArray()
    }

    private fun encodeBase64UrlSafe(data : ByteArray) : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getUrlEncoder().withoutPadding().encodeToString(data)
        }
        else {
            @Suppress("DEPRECATION") android.util.Base64.encodeToString(data , android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP).trimEnd('=')
        }
    }
}