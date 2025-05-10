package com.d4rk.cartcalculator.app.cart.list.domain.usecases

import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.GZIPInputStream

class DecryptSharedCartUseCase : Repository<String , Flow<DataState<Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> , Errors>>> {

    override suspend fun invoke(param : String) : Flow<DataState<Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> , Errors>> = flow {
        runCatching {
            val uri : Uri = param.toUri()
            val encoded : String = uri.getQueryParameter("d") ?: error(message = "Missing 'd'")
            val decoded : ByteArray = decodeBase64UrlSafe(encoded = encoded)
            val decompressed : ByteArray = decompressGzip(input = decoded)
            val raw : String = decompressed.decodeToString()

            parseRawData(raw = raw)
        }.onSuccess {
            emit(value = DataState.Success(data = it))
        }.onFailure {
            emit(value = DataState.Error(error = it.toError()))
        }
    }

    private fun decompressGzip(input : ByteArray) : ByteArray {
        val output = ByteArrayOutputStream()
        GZIPInputStream(input.inputStream()).use { it.copyTo(output) }
        return output.toByteArray()
    }

    private fun decodeBase64UrlSafe(encoded : String) : ByteArray {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getUrlDecoder().decode(encoded)
        }
        else {
            @Suppress("DEPRECATION") android.util.Base64.decode(encoded , android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP)
        }
    }

    private fun parseRawData(raw : String) : Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> {
        val map : Map<String , String> = raw.split(";").associate {
            val (key : String , value : String) = it.split("=")
            key to value
        }

        val cart = ShoppingCartTable(
            cartId = map["id"]?.toIntOrNull() ?: 0 ,
            name = map["name"]?.replace(oldValue = "_" , newValue = " ") ?: "Untitled" ,
            date = map["date"]?.toLongOrNull() ?: 0L ,
            sharedCart = map["shared"] == "true" ,
        )

        val cartItems = ShoppingCartItemsTable(
            itemId = map["iid"]?.toIntOrNull() ?: 0 ,
            cartId = cart.cartId,
            name = map["iname"]?.replace(oldValue = "_" , newValue = " ") ?: "Unknown Item" ,
            quantity = map["quantity"]?.toIntOrNull() ?: 0,
            price = map["price"] ?: "0.00",
            isChecked = map["checked"] == "true"
        )

        return cart to listOf<ShoppingCartItemsTable>(element = cartItems)
    }
}