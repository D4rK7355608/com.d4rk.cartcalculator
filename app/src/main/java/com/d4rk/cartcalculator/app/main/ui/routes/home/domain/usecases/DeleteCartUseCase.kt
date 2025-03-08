package com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases

import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.DataState
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteCartUseCase(private val database: DatabaseInterface) : Repository<ShoppingCartTable , Flow<DataState<Unit , Errors>>> {
    override suspend fun invoke(param: ShoppingCartTable): Flow<DataState<Unit, Errors>> = flow {
        emit(value = DataState.Loading())

        runCatching {
            database.deleteCart(cart = param)
            database.deleteItemsFromCart(cartId = param.cartId)
        }.onSuccess {
           emit(value = DataState.Success(data = Unit))
        }.onFailure {
            emit(value = DataState.Error(error = Errors.UseCase.FAILED_TO_DELETE_CART))
        }
    }
}