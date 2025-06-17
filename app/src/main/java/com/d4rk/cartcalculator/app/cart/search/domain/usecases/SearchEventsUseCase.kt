package com.d4rk.cartcalculator.app.cart.search.domain.usecases


import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.RootError
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchEventsUseCase(
    private val database: DatabaseInterface
) : Repository<String, Flow<DataState<List<ShoppingCartTable>, RootError>>> {

    override suspend fun invoke(param: String): Flow<DataState<List<ShoppingCartTable>, RootError>> = flow {
        emit(DataState.Loading())
        runCatching {
            val searchResults = database.searchCartsByName(param)
            emit(DataState.Success(searchResults))
        }.onFailure { e ->
            emit(DataState.Error(error = e.toError(default = Errors.Database.DATABASE_OPERATION_FAILED)))
        }
    }
}