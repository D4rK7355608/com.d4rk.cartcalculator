package com.d4rk.cartcalculator.app.cart.search.domain.usecases


import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.RootError
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchEventsUseCase(
    private val database: DatabaseInterface
) : Repository<String, Flow<DataState<List<EventsListTable>, RootError>>> {

    override suspend fun invoke(param: String): Flow<DataState<List<EventsListTable>, RootError>> = flow {
        emit(DataState.Loading())
        try {
            // Perform the actual database search
            val searchResults = database.searchEventsByName(param)
            emit(DataState.Success(searchResults))
        } catch (e: Exception) {
            // Handle potential database errors (e.g., SQLiteConstraintException, etc.)
            // You might want to map specific database exceptions to your RootError types
            emit(DataState.Error(error = e.toError(default = Errors.Database.DATABASE_OPERATION_FAILED)))
        }
    }
}