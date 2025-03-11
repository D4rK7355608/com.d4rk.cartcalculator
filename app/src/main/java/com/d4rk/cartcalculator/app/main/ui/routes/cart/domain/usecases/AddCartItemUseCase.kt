package com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddCartItemUseCase(private val database : DatabaseInterface) : Repository<ShoppingCartItemsTable , Flow<DataState<ShoppingCartItemsTable , Errors>>> {

    override suspend fun invoke(param : ShoppingCartItemsTable) : Flow<DataState<ShoppingCartItemsTable , Errors>> = flow {
        runCatching {
            val newItemId : Long = database.insertItem(item = param)
            param.copy(itemId = newItemId.toInt())
        }.onSuccess { newItem ->
            emit(value = DataState.Success(data = newItem))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_ADD_CART_ITEM)))
        }
    }
}