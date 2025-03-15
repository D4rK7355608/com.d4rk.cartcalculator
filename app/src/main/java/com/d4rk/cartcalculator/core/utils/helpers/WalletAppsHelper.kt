package com.d4rk.cartcalculator.core.utils.helpers

import android.content.Context
import android.widget.Toast
import com.d4rk.android.libs.apptoolkit.R

object WalletAppsHelper : AppInfoHelper() {

    private const val GOOGLE_PAY_PACKAGE : String = "com.google.android.apps.walletnfcrel"
    private const val GOOGLE_WALLET_PACKAGE : String = "com.google.android.apps.nbu.paisa.user"

    /**
     * Checks if Google Pay is installed.
     */
    fun isGooglePayInstalled(context : Context) : Boolean = isAppInstalled(context = context , packageName = GOOGLE_PAY_PACKAGE)

    /**
     * Checks if Google Wallet is installed.
     */
    private fun isGoogleWalletInstalled(context : Context) : Boolean = isAppInstalled(context = context , packageName = GOOGLE_WALLET_PACKAGE)

    /**
     * Opens Google Pay if installed, otherwise Google Wallet.
     */
    fun openGooglePayOrWallet(context : Context) {
        val packageName = when {
            isGooglePayInstalled(context = context) -> GOOGLE_PAY_PACKAGE
            isGoogleWalletInstalled(context = context) -> GOOGLE_WALLET_PACKAGE
            else -> null
        }

        packageName?.let {
            openApp(context = context , packageName = it)
        } ?: Toast.makeText(context , "Google Pay/Wallet is not installed" , Toast.LENGTH_SHORT).show()
    }
}

open class AppInfoHelper {

    /**
     * Checks if a specific app is installed on the device.
     * @return True if the app is installed, false otherwise.
     */
    fun isAppInstalled(context : Context , packageName : String) : Boolean = runCatching { context.packageManager.getApplicationInfo(packageName , 0) }.isSuccess

    /**
     * Opens a specific app if installed.
     * @return True if the app was opened, false otherwise.
     */
    fun openApp(context : Context , packageName : String) : Boolean = runCatching {
        context.packageManager.getLaunchIntentForPackage(packageName)?.let {
            context.startActivity(it)
            true
        } ?: false
    }.getOrElse {
        Toast.makeText(context , context.getString(R.string.app_not_installed) , Toast.LENGTH_SHORT).show()
        false
    }
}