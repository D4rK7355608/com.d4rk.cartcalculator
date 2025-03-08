package com.d4rk.cartcalculator.core.domain.model.network

sealed interface Errors: Error {

    enum class Network: Errors {
        REQUEST_TIMEOUT,
        NO_INTERNET,
        SERVER_ERROR,
        SERIALIZATION
    }

    enum class UseCase : Errors {
        NO_DATA,
        EMPTY_CART,
        COMPRESSION_FAILED,
        CART_NOT_FOUND,
        FAILED_TO_IMPORT_CART,
        FAILED_TO_DELETE_CART,
        FAILED_TO_DECRYPT_CART,
        FAILED_TO_ENCRYPT_CART,
    }

    enum class Database : Errors {
        DATABASE_OPERATION_FAILED,
        CART_NOT_FOUND
    }
}
