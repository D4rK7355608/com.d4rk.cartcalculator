package com.d4rk.cartcalculator.app.cart.details.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateCartItemUseCase(private val database : DatabaseInterface) : Repository<ShoppingCartItemsTable , Flow<DataState<ShoppingCartItemsTable , Errors>>> {

    override suspend fun invoke(param : ShoppingCartItemsTable) : Flow<DataState<ShoppingCartItemsTable , Errors>> = flow {
        runCatching {
            database.updateItem(item = param)
            param
        }.onSuccess { updatedItem ->
            emit(value = DataState.Success(data = updatedItem))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_UPDATE_CART_ITEM)))
        }
    }
}