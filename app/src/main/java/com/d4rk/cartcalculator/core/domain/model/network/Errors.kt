package com.d4rk.cartcalculator.core.domain.model.network

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Error

sealed interface Errors : Error {

    enum class Network : Errors {
        REQUEST_TIMEOUT , NO_INTERNET , SERVER_ERROR , SERIALIZATION
    }

    enum class UseCase : Errors {
        NO_DATA , EMPTY_CART , COMPRESSION_FAILED , CART_NOT_FOUND , FAILED_TO_IMPORT_CART , FAILED_TO_DELETE_CART , FAILED_TO_DECRYPT_CART , FAILED_TO_ENCRYPT_CART , FAILED_TO_LOAD_CART , FAILED_TO_UPDATE_CART_ITEM , FAILED_TO_DELETE_CART_ITEM , FAILED_TO_ADD_CART_ITEM
    }

    enum class Database : Errors {
        DATABASE_OPERATION_FAILED , CART_NOT_FOUND
    }
}
