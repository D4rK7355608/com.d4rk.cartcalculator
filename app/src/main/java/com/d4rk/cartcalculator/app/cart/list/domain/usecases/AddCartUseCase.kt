package com.d4rk.cartcalculator.app.cart.list.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddCartUseCase(private val database : DatabaseInterface) : Repository<ShoppingCartTable , Flow<DataState<ShoppingCartTable , Errors>>> {

    override suspend fun invoke(param : ShoppingCartTable) : Flow<DataState<ShoppingCartTable , Errors>> = flow {
        runCatching {
            val newCartId : Long = database.insertCart(cart = param)
            param.copy(cartId = newCartId.toInt())
        }.onSuccess { newCart ->
            emit(value = DataState.Success(data = newCart))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.Database.DATABASE_OPERATION_FAILED)))
        }
    }
}