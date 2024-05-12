package com.d4rk.cartcalculator.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.d4rk.cartcalculator.R

/**
 * A utility object for performing common operations such as opening URLs, activities, and app notification settings.
 *
 * This object provides functions to open a URL in the default browser, open an activity, and open the app's notification settings.
 * All operations are performed in the context of an Android application.
 */
object Utils {

    /**
     * Opens a specified URL in the default browser.
     *
     * This function creates an Intent with the ACTION_VIEW action and the specified URL, and starts an activity with this intent.
     * The activity runs in a new task.
     *
     * @param context The Android context in which the URL should be opened.
     * @param url The URL to open.
     */
    fun openUrl(context: Context, url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens a specified activity.
     *
     * This function creates an Intent with the specified activity class, and starts an activity with this intent. The activity runs in a new task.
     *
     * @param context The Android context in which the activity should be opened.
     * @param activityClass The class of the activity to open.
     */
    fun openActivity(context: Context, activityClass: Class<*>) {
        Intent(context, activityClass).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens the app's notification settings.
     *
     * This function creates an Intent with the ACTION_APP_NOTIFICATION_SETTINGS action and the app's package name, and starts an activity with this intent.
     * The activity runs in a new task.
     *
     * @param context The Android context in which the app's notification settings should be opened.
     */
    fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Opens the app locale settings if available, otherwise opens the app details settings.
     *
     * @param context The Android context.
     */
    fun openAppLocaleSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeIntent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).setData(
                Uri.fromParts("package", context.packageName, null)
            )
            val detailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                Uri.fromParts("package", context.packageName, null)
            )
            when {
                context.packageManager.resolveActivity(
                    localeIntent,
                    0
                ) != null -> context.startActivity(localeIntent)

                context.packageManager.resolveActivity(
                    detailsIntent,
                    0
                ) != null -> context.startActivity(detailsIntent)

                else -> {
                    // TODO: Handle the case where neither Intent can be resolved
                }
            }
        } else {
            // TODO: Handle the case for Android versions lower than 13
        }
    }

    /**
     * Opens the app's share sheet.
     *
     * This function creates an Intent with the ACTION_SEND action and a share message containing the app's name and a link to the app's Play Store listing.
     * The intent is then wrapped in a chooser Intent to allow the user to select an app to share the message with.
     *
     * @param context The Android context in which the share sheet should be opened.
     */
    fun shareApp(context: Context) {
        val shareMessage = context.getString(
            R.string.summary_share_message,
            "https://play.google.com/store/apps/details?id=${context.packageName}"
        )
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        context.startActivity(
            Intent.createChooser(
                shareIntent, context.resources.getText(R.string.send_email_using)
            )
        )
    }
}