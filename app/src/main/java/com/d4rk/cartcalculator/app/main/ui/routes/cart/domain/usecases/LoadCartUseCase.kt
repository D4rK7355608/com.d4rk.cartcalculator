package com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoadCartUseCase(private val database: DatabaseInterface) : Repository<Int , Flow<DataState<Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> , Errors>>> {
    override suspend fun invoke(param: Int): Flow<DataState<Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> , Errors>> = flow {
        runCatching {
            val cart : ShoppingCartTable = database.getCartById(cartId = param) ?: throw IllegalArgumentException("Cart not found")
            val items : List<ShoppingCartItemsTable> = database.getItemsByCartId(cartId = param)
            emit(value = DataState.Success(data = Pair(first = cart , second = items)))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_LOAD_CART)))
        }
    }
}