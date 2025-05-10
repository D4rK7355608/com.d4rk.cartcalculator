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
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.GZIPOutputStream

class GenerateCartShareLinkUseCase(
    private val database : DatabaseInterface
) : Repository<Int , Flow<DataState<String , Errors>>> {

    override suspend fun invoke(param : Int) : Flow<DataState<String , Errors>> = flow {
        runCatching {
            val cart = database.getCartById(cartId = param) ?: error("Cart not found")
            val items = database.getItemsByCartId(cartId = param)
            if (items.isEmpty()) error("Empty cart")

            val rawString = flatten(cart , items)
            val compressed = compressGzip(rawString.encodeToByteArray())
            val encoded = encodeBase64UrlSafe(compressed)

            "https://cartcalculator/i?d=$encoded"
        }.onSuccess {
            emit(value = DataState.Success(data = it))
        }.onFailure {
            emit(value = DataState.Error(error = it.toError()))
        }
    }

    private fun flatten(cart : ShoppingCartTable , items : List<ShoppingCartItemsTable>) : String {
        val header = listOf(
            "id=${cart.cartId}" , "name=${cart.name.replace(" " , "_")}" , "date=${cart.date}" , "shared=true"
        )

        val itemChunks = items.joinToString(separator = "|") { item ->
            listOf(
                "iid=${item.itemId}" , "iname=${item.name.replace(" " , "_")}" , "quantity=${item.quantity}" , "price=${item.price}" , "checked=${item.isChecked}"
            ).joinToString(";")
        }

        return header.joinToString(";") + ";;" + itemChunks // Split cart from items with ';;'
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