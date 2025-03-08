package com.d4rk.cartcalculator.core.domain.model.network

typealias RootError = Error

sealed interface DataState<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data : D) : DataState<D , E>
    data class Error<out D, out E : RootError>(val data: D? = null , val error: E) : DataState<D , E>
    data class Loading<out D, out E : RootError>(val data: D? = null) : DataState<D , E>
    data class Update<out D, out E : RootError>(val data: D? = null) : DataState<D , E>
}

inline fun <D, E : RootError> DataState<D , E>.onSuccess(action: (D) -> Unit): DataState<D , E> {
    return when (this) {
        is DataState.Success -> {
            action(data)
            this
        }
        is DataState.Update -> this
        is DataState.Loading -> this
        is DataState.Error -> this

    }
}

inline fun <D, E : Error> DataState<D , E>.onError(action: (E) -> Unit): DataState<D , E> {
    return when (this) {
        is DataState.Error -> {
            action(error)
            this
        }
        is DataState.Loading -> this
        is DataState.Success -> this
        is DataState.Update -> this
    }
}

inline fun <D, E : Error> DataState<D , E>.onLoading(action: (D?) -> Unit): DataState<D , E> {
    return when (this) {
        is DataState.Loading -> {
            action(data)
            this
        }
        is DataState.Error -> this
        is DataState.Success -> this
        is DataState.Update -> this
    }
}

inline fun <D, E : Error> DataState<D , E>.onUpdate(action: (D?) -> Unit): DataState<D , E> {
    return when (this) {
        is DataState.Update -> {
            action(data)
            this
        }
        is DataState.Error -> this
        is DataState.Loading -> this
        is DataState.Success -> this
    }
}
