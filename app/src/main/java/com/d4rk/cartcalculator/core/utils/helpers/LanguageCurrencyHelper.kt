package com.d4rk.cartcalculator.core.utils.helpers

import android.content.Context
import android.os.LocaleList
import com.d4rk.cartcalculator.R
import java.util.Locale

object LanguageCurrencyHelper {

    private val languageToCurrency : Map<String , String> = mapOf<String , String>(
        "bg" to "EUR" ,
        "de" to "EUR" ,
        "es" to "EUR" ,
        "fr" to "EUR" ,
        "it" to "EUR" ,
        "ro" to "RON" ,
        "pt" to "BRL" ,
        "ru" to "RUB" ,
        "hi" to "INR" ,
        "in" to "IDR" ,
        "ja" to "JPY" ,
        "pl" to "PLN" ,
        "th" to "THB" ,
        "tr" to "TRY" ,
        "uk" to "UAH" ,
        "zh-hant" to "HKD" ,
        "hu" to "HUF"
    )

    fun getDefaultCurrencyForLocale(applicationContext : Context) : String? {
        val deviceLanguage : String = getSystemLanguage()
        val supportedLanguageCodes = applicationContext.resources.getStringArray(com.d4rk.android.libs.apptoolkit.R.array.preference_language_values)
        val matchedLanguageCode : String? = supportedLanguageCodes.find { deviceLanguage.startsWith(prefix = it) }
        val currencyCodeHint : String? = matchedLanguageCode?.let { languageToCurrency[it] }
        val availableCurrencyCodes = applicationContext.resources.getStringArray(R.array.currency)
        val matchedCurrencyCode : String? = currencyCodeHint?.let { hint : String ->
            availableCurrencyCodes.firstOrNull { it.startsWith(prefix = hint) }
        }

        return matchedCurrencyCode
    }

    private fun getSystemLanguage() : String {
        val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            LocaleList.getDefault().get(0)
        }
        else {
            Locale.getDefault()
        }
        return locale.toLanguageTag().replace(oldValue = "-r" , newValue = "-").lowercase()
    }
}