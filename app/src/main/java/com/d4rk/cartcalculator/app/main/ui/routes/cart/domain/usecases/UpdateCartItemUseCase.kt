package com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases

import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.domain.model.network.DataState
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateCartItemUseCase(
    private val database : DatabaseInterface
) : Repository<ShoppingCartItemsTable , Flow<DataState<ShoppingCartItemsTable , Errors>>> {

    override suspend fun invoke(param : ShoppingCartItemsTable) : Flow<DataState<ShoppingCartItemsTable , Errors>> = flow {
        emit(DataState.Loading())
        runCatching {
            database.updateItem(param)
            param
        }.onSuccess { updatedItem ->
            emit(DataState.Success(updatedItem))
        }.onFailure { throwable ->
            emit(DataState.Error(error = throwable.toError(Errors.UseCase.FAILED_TO_UPDATE_CART_ITEM)))
        }
    }
}