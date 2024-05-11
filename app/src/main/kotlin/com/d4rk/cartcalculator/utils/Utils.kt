package com.d4rk.cartcalculator.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Utility object for common operations.
 */
object Utils {

    /**
     * Opens a URL in the default browser.
     *
     * @param context The Android context.
     * @param url The URL to open.
     */
    fun openUrl(context : Context , url : String) {
        Intent(Intent.ACTION_VIEW , Uri.parse(url)).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens an activity.
     *
     * @param context The Android context.
     * @param activityClass The class of the activity to open.
     */
    fun openActivity(context : Context , activityClass : Class<*>) {
        Intent(context , activityClass).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens the app notification settings.
     *
     * @param context The Android context.
     */
    fun openAppNotificationSettings(context : Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE , context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Opens the app locale settings if available, otherwise opens the app details settings.
     *
     * @param context The Android context.
     */
    fun openAppLocaleSettings(context : Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeIntent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).setData(
                Uri.fromParts("package", context.packageName, null)
            )
            val detailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                Uri.fromParts("package", context.packageName, null)
            )
            when {
                context.packageManager.resolveActivity(localeIntent, 0) != null -> context.startActivity(localeIntent)
                context.packageManager.resolveActivity(detailsIntent, 0) != null -> context.startActivity(detailsIntent)
                else -> {
                    // TODO: Handle the case where neither Intent can be resolved
                }
            }
        } else {
            // TODO: Handle the case for Android versions lower than 13
        }
    }
}