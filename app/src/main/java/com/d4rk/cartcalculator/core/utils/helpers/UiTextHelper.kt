package com.d4rk.cartcalculator.core.utils.helpers

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

sealed class UiTextHelper {
    data class DynamicString(val content: String) : UiTextHelper()
    data class StringResource(val resourceId: Int , val arguments: List<Any> = emptyList()) : UiTextHelper()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> content
            is StringResource -> context.getString(resourceId , *arguments.toTypedArray())
        }
    }

    @Composable
    fun asString(): String {
        val context : Context = LocalContext.current.applicationContext
        return when (this) {
            is DynamicString -> content
            is StringResource -> context.getString(resourceId , *arguments.toTypedArray())
        }
    }
}