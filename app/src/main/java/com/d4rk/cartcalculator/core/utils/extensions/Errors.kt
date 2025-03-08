package com.d4rk.cartcalculator.core.utils.extensions

import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.utils.helpers.UiTextHelper

fun Errors.asUiText(): UiTextHelper {
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
    }
}
