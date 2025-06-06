package com.d4rk.cartcalculator.app.cart.list.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCartsUseCase(private val database : DatabaseInterface) : Repository<Unit , Flow<DataState<List<ShoppingCartTable> , Errors>>> {
    override suspend fun invoke(param : Unit) : Flow<DataState<List<ShoppingCartTable> , Errors>> = flow {
        runCatching {
            database.getAllCarts()
        }.onSuccess { carts ->
            emit(value = DataState.Success(data = carts))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError()))
        }
    }
}