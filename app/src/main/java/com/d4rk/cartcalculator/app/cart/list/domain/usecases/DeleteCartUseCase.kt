package com.d4rk.cartcalculator.app.cart.list.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteCartUseCase(private val database : DatabaseInterface) : Repository<ShoppingCartTable , Flow<DataState<Unit , Errors>>> {
    override suspend fun invoke(param : ShoppingCartTable) : Flow<DataState<Unit , Errors>> = flow {
        runCatching {
            database.deleteCart(cart = param)
            database.deleteItemsFromCart(cartId = param.cartId)
        }.onSuccess {
            emit(value = DataState.Success(data = Unit))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_DELETE_CART)))
        }
    }
}