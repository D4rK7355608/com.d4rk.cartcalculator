package com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases

import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.domain.model.network.DataState
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImportSharedCartUseCase(
    private val database : DatabaseInterface , private val decryptSharedCartUseCase : DecryptSharedCartUseCase
) : Repository<String , Flow<DataState<Unit , Errors>>> {

    override suspend fun invoke(param : String) : Flow<DataState<Unit , Errors>> = flow {
        emit(DataState.Loading())

        runCatching {
            decryptSharedCartUseCase(param).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val (cart , items) = result.data

                        var newCartName = cart.name
                        val allCarts = database.getAllCarts()
                        var suffix = 1

                        while (allCarts.any { it.name.equals(newCartName , ignoreCase = true) }) {
                            newCartName = "${cart.name} ($suffix)"
                            suffix ++
                        }

                        val newCart = cart.copy(cartId = 0 , name = newCartName)
                        val newCartId = database.insertCart(newCart)

                        items.forEach { item ->
                            val newItem = item.copy(itemId = 0 , cartId = newCartId.toInt())
                            database.insertItem(newItem)
                        }

                        emit(DataState.Success(Unit))
                    }

                    is DataState.Error -> emit(DataState.Error(error = result.error))
                    is DataState.Loading -> emit(DataState.Loading())
                    else -> {}
                }
            }
        }.onFailure { exception ->
            emit(DataState.Error(error = exception.toError(Errors.UseCase.FAILED_TO_IMPORT_CART)))
        }
    }
}