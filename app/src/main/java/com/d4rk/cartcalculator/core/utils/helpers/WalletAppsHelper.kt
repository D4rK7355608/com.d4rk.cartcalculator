package com.d4rk.cartcalculator.core.utils.helpers

import android.content.Context
import android.widget.Toast
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.AppInfoHelper
import com.d4rk.cartcalculator.R

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
        } ?: Toast.makeText(context , context.getString(R.string.google_pay_wallet_not_installed) , Toast.LENGTH_SHORT).show()
    }
}