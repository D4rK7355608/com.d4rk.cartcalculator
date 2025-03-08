package com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases

import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.DataState
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddCartUseCase(private val database: DatabaseInterface) : Repository<ShoppingCartTable , Flow<DataState<ShoppingCartTable , Errors>>> {

    override suspend fun invoke(param: ShoppingCartTable): Flow<DataState<ShoppingCartTable, Errors>> = flow {
        emit(value = DataState.Loading())

        runCatching {
            val newCartId: Long = database.insertCart(cart = param)
            param.copy(cartId = newCartId.toInt())
        }.onSuccess { newCart ->
            emit(value = DataState.Success(newCart))
        }.onFailure {
            emit(value = DataState.Error(error = Errors.Database.DATABASE_OPERATION_FAILED))
        }
    }
}