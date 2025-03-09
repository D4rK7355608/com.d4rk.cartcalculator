package com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.usecases

import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.DataState
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoadCartUseCase(
    private val database : DatabaseInterface
) : Repository<Int , Flow<DataState<Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> , Errors>>> {

    override suspend fun invoke(param : Int) : Flow<DataState<Pair<ShoppingCartTable , List<ShoppingCartItemsTable>> , Errors>> = flow {
        emit(DataState.Loading())
        runCatching {
            val cart = database.getCartById(cartId = param) ?: throw IllegalArgumentException("Cart not found")
            val items = database.getItemsByCartId(cartId = param)
            Pair(cart , items)
        }.onSuccess { data ->
            emit(DataState.Success(data))
        }.onFailure { throwable ->
            emit(DataState.Error(error = throwable.toError(Errors.UseCase.FAILED_TO_LOAD_CART)))
        }
    }
}