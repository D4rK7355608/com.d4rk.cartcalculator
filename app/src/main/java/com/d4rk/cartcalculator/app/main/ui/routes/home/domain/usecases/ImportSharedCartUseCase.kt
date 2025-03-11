package com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases

import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImportSharedCartUseCase(private val database : DatabaseInterface , private val decryptSharedCartUseCase : DecryptSharedCartUseCase) : Repository<String , Flow<DataState<Unit , Errors>>> {

    override suspend fun invoke(param : String) : Flow<DataState<Unit , Errors>> = flow {
        runCatching {
            decryptSharedCartUseCase(param).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val (cart : ShoppingCartTable , items : List<ShoppingCartItemsTable>) = result.data
                        var newCartName : String = cart.name
                        val allCarts : List<ShoppingCartTable> = database.getAllCarts()
                        var suffix = 1

                        while (allCarts.any { it.name.equals(other = newCartName , ignoreCase = true) }) {
                            newCartName = "${cart.name} ($suffix)"
                            suffix ++
                        }
                        val newCart : ShoppingCartTable = cart.copy(cartId = 0 , name = newCartName)
                        val newCartId : Long = database.insertCart(cart = newCart)
                        items.forEach { item ->
                            val newItem : ShoppingCartItemsTable = item.copy(itemId = 0 , cartId = newCartId.toInt())
                            database.insertItem(item = newItem)
                        }
                    }

                    is DataState.Error -> {
                        emit(value = DataState.Error(error = result.error))
                    }
                    is DataState.Loading -> {
                        emit(value = DataState.Loading())
                    }
                    else -> {}
                }
            }
        }.onSuccess {
            emit(value = DataState.Success(data = Unit))
        }.onFailure { exception ->
            emit(value = DataState.Error(error = exception.toError(default = Errors.UseCase.FAILED_TO_IMPORT_CART)))
        }
    }
}