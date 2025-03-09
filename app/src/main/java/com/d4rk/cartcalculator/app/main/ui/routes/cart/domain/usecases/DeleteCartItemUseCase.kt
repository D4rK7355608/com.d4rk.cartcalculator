package com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases

import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.domain.model.network.DataState
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteCartItemUseCase(
    private val database: DatabaseInterface
) : Repository<ShoppingCartItemsTable, Flow<DataState<Unit, Errors>>> {

    override suspend fun invoke(param: ShoppingCartItemsTable): Flow<DataState<Unit, Errors>> = flow {
        emit(DataState.Loading())
        runCatching {
            database.deleteItem(param)
        }.onSuccess {
            emit(DataState.Success(Unit))
        }.onFailure { throwable ->
            emit(DataState.Error(error = throwable.toError(Errors.UseCase.FAILED_TO_DELETE_CART_ITEM)))
        }
    }
}