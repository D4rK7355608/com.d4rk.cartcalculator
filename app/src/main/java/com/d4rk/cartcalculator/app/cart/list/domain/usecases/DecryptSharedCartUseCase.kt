package com.d4rk.cartcalculator.app.cart.list.domain.usecases

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
            val uri = param.toUri()
            val encoded = uri.getQueryParameter("d") ?: error("Missing 'd'")
            val decoded = decodeBase64UrlSafe(encoded)
            val decompressed = decompressGzip(decoded)
            val raw = decompressed.decodeToString()

            parseRawData(raw)
        }.onSuccess {
            emit(DataState.Success(it))
        }.onFailure {
            emit(DataState.Error(error = it.toError()))
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
        val parts = raw.split(";;")
        val cartPart = parts.getOrNull(0) ?: ""
        val itemsPart = parts.getOrNull(1).orEmpty()

        val map = cartPart.split(";").associate {
            val (key , value) = it.split("=")
            key to value.replace("_" , " ")
        }

        val cart = ShoppingCartTable(
            cartId = map["id"]?.toIntOrNull() ?: 0 , name = map["name"] ?: "Untitled" , date = map["date"]?.toLongOrNull() ?: 0L , sharedCart = map["shared"] == "true"
        )

        val items = itemsPart.split("|").mapNotNull { itemEntry ->
            runCatching {
                val itemMap = itemEntry.split(";").associate {
                    val (key , value) = it.split("=")
                    key to value.replace("_" , " ")
                }

                ShoppingCartItemsTable(itemId = itemMap["iid"]?.toIntOrNull() ?: 0 , cartId = cart.cartId , name = itemMap["iname"] ?: "Item" , quantity = itemMap["quantity"]?.toIntOrNull() ?: 1 , price = itemMap["price"] ?: "0.00" , isChecked = itemMap["checked"] == "true")
            }.getOrNull()
        }

        return cart to items
    }
}