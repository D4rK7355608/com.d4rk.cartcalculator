package com.d4rk.cartcalculator.utils.external

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast

object AppUtils {

    private const val GOOGLE_PAY_PACKAGE = "com.google.android.apps.walletnfcrel"
    private const val GOOGLE_WALLET_PACKAGE = "com.google.android.apps.nbu.paisa.user"

    /**
     * Checks if a specific app is installed on the device.
     * @param context The application context.
     * @param packageName The package name of the app to check.
     * @return True if the app is installed, false otherwise.
     */
    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Checks if Google Pay is installed.
     */
    fun isGooglePayInstalled(context: Context): Boolean {
        return isAppInstalled(context, GOOGLE_PAY_PACKAGE)
    }

    /**
     * Checks if Google Wallet is installed.
     */
    private fun isGoogleWalletInstalled(context: Context): Boolean {
        return isAppInstalled(context, GOOGLE_WALLET_PACKAGE)
    }

    /**
     * Opens a specific app if installed.
     * @param context The application context.
     * @param packageName The package name of the app to open.
     * @return True if the app was opened, false if not installed.
     */
    private fun openApp(context: Context, packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            context.startActivity(intent)
            true
        } else {
            Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Opens Google Pay if installed, otherwise Google Wallet.
     */
    fun openGooglePayOrWallet(context: Context) {
        when {
            isGooglePayInstalled(context) -> openApp(context, GOOGLE_PAY_PACKAGE)
            isGoogleWalletInstalled(context) -> openApp(context, GOOGLE_WALLET_PACKAGE)
            else -> Toast.makeText(context, "Google Pay/Wallet is not installed", Toast.LENGTH_SHORT).show()
        }
    }
}