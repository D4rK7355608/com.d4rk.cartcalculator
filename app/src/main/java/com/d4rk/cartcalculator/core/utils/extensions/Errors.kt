package com.d4rk.cartcalculator.core.utils.extensions

import android.database.sqlite.SQLiteException
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.helpers.UiTextHelper
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.sql.SQLException

fun Errors.asUiText() : UiTextHelper {
    return when (this) {
        // Network errors
        Errors.Network.NO_INTERNET -> UiTextHelper.StringResource(R.string.request_no_internet)
        Errors.Network.REQUEST_TIMEOUT -> UiTextHelper.StringResource(R.string.request_timeout)
        Errors.Network.SERVER_ERROR -> UiTextHelper.StringResource(R.string.request_server_not_found)
        Errors.Network.SERIALIZATION -> UiTextHelper.StringResource(R.string.request_payload_error)

        // Cart errors
        Errors.UseCase.NO_DATA -> UiTextHelper.StringResource(R.string.no_carts_available)
        Errors.UseCase.FAILED_TO_DELETE_CART -> UiTextHelper.StringResource(R.string.failed_to_delete_cart)
        Errors.UseCase.FAILED_TO_DECRYPT_CART -> UiTextHelper.StringResource(R.string.failed_to_decrypt_cart)
        Errors.UseCase.EMPTY_CART -> UiTextHelper.StringResource(R.string.empty_cart)
        Errors.UseCase.COMPRESSION_FAILED -> UiTextHelper.StringResource(R.string.compression_failed)
        Errors.UseCase.CART_NOT_FOUND -> UiTextHelper.StringResource(R.string.cart_not_found)
        Errors.UseCase.FAILED_TO_IMPORT_CART -> UiTextHelper.StringResource(R.string.failed_to_import_cart)
        Errors.UseCase.FAILED_TO_ENCRYPT_CART -> UiTextHelper.StringResource(R.string.failed_to_encrypt_cart)

        // Database errors
        Errors.Database.DATABASE_OPERATION_FAILED -> UiTextHelper.StringResource(R.string.database_operation_failed)
        Errors.Database.CART_NOT_FOUND -> UiTextHelper.StringResource(R.string.database_cart_not_found)
        Errors.UseCase.FAILED_TO_LOAD_CART -> UiTextHelper.StringResource(R.string.failed_to_load_cart)
        Errors.UseCase.FAILED_TO_UPDATE_CART_ITEM -> UiTextHelper.StringResource(R.string.failed_to_update_cart_item)
        Errors.UseCase.FAILED_TO_DELETE_CART_ITEM -> UiTextHelper.StringResource(R.string.failed_to_delete_cart_item)
        Errors.UseCase.FAILED_TO_ADD_CART_ITEM -> UiTextHelper.StringResource(R.string.failed_to_add_cart_item)
    }
}

fun Throwable.toError(default : Errors = Errors.UseCase.NO_DATA) : Errors {
    return when (this) {
        is UnknownHostException -> Errors.Network.NO_INTERNET
        is SocketTimeoutException -> Errors.Network.REQUEST_TIMEOUT
        is ConnectException -> Errors.Network.NO_INTERNET
        is SerializationException -> Errors.Network.SERIALIZATION
        is SQLException , is SQLiteException -> Errors.Database.DATABASE_OPERATION_FAILED
        is IllegalStateException -> when (this.message) {
            Errors.UseCase.CART_NOT_FOUND.toString() -> Errors.UseCase.CART_NOT_FOUND
            Errors.UseCase.EMPTY_CART.toString() -> Errors.UseCase.EMPTY_CART
            Errors.UseCase.COMPRESSION_FAILED.toString() -> Errors.UseCase.COMPRESSION_FAILED
            Errors.UseCase.FAILED_TO_IMPORT_CART.toString() -> Errors.UseCase.FAILED_TO_IMPORT_CART
            else -> Errors.UseCase.FAILED_TO_ENCRYPT_CART
        }

        is IllegalArgumentException -> Errors.UseCase.FAILED_TO_DECRYPT_CART
        else -> default
    }
}