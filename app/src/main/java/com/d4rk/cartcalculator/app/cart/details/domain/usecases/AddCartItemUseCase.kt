package com.d4rk.cartcalculator.app.cart.details.domain.usecases

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
            val newItemId : Long = database.insertItem(item = param.copy(cartId = param.cartId))
            param.copy(itemId = newItemId.toInt() , cartId = param.cartId)
        }.onSuccess { newItem : ShoppingCartItemsTable ->
            emit(value = DataState.Success(data = newItem))
        }.onFailure { throwable : Throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_ADD_CART_ITEM)))
        }
    }
}