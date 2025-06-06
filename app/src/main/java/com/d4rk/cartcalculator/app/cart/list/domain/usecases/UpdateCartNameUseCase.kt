package com.d4rk.cartcalculator.app.cart.list.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateCartNameUseCase(private val database : DatabaseInterface) : Repository<Pair<ShoppingCartTable , String> , Flow<DataState<ShoppingCartTable , Errors>>> {
    override suspend fun invoke(param : Pair<ShoppingCartTable , String>) : Flow<DataState<ShoppingCartTable , Errors>> = flow {
        runCatching {
            val (cart : ShoppingCartTable , newName : String) = param
            val updatedCart : ShoppingCartTable = cart.copy(name = newName)
            database.updateCart(cart = updatedCart)
            updatedCart
        }.onSuccess { cart : ShoppingCartTable ->
            emit(value = DataState.Success(data = cart))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.Database.DATABASE_OPERATION_FAILED)))
        }
    }
}