package com.d4rk.cartcalculator.core.data.datastore

import android.content.Context
import android.os.LocaleList
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.utils.constants.datastore.AppDataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale

class DataStore(val context : Context) : CommonDataStore(context = context) {

    private val hasSeenConfettiKey : Preferences.Key<Boolean> = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_HAS_SEEN_CONFETTI)

    fun hasSeenConfetti() : Flow<Boolean> {
        return dataStore.data.map { preferences : Preferences ->
            preferences[hasSeenConfettiKey] == true
        }
    }

    suspend fun saveHasSeenConfetti(seen : Boolean) {
        dataStore.edit { preferences : MutablePreferences ->
            preferences[hasSeenConfettiKey] = seen
        }
    }

    private val currencyKey : Preferences.Key<String> = stringPreferencesKey(name = AppDataStoreConstants.DATA_STORE_PREFERRED_CURRENCY)

    fun getCurrency(): Flow<String> = dataStore.data.map { it[currencyKey] ?: "" }

    suspend fun saveCurrency(currency: String) {
        val current = getCurrency().first()
        if (current.isBlank()) {
            val autoCurrency = LanguageCurrencyHelper.getDefaultCurrencyForLocale(context)
            dataStore.edit { it[currencyKey] = autoCurrency ?: currency }
        } else {
            dataStore.edit { it[currencyKey] = currency }
        }
    }

    private val openCartsAfterCreationKey : Preferences.Key<Boolean> = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_OPEN_CARTS_AFTER_CREATION)

    val openCartsAfterCreation : Flow<Boolean> = dataStore.data.map { preferences : Preferences ->
        preferences[openCartsAfterCreationKey] != false
    }

    suspend fun saveOpenCartsAfterCreation(isEnabled : Boolean) {
        dataStore.edit { preferences : MutablePreferences ->
            preferences[openCartsAfterCreationKey] = isEnabled
        }
    }
}

object LanguageCurrencyHelper {

    private val languageToCurrencyCode = mapOf(
        "bg" to "EUR", "de" to "EUR", "es" to "EUR", "fr" to "EUR", "it" to "EUR",
        "ro" to "RON", "pt" to "BRL", "ru" to "RUB", "hi" to "INR", "in" to "IDR",
        "ja" to "JPY", "pl" to "PLN", "th" to "THB", "tr" to "TRY", "uk" to "UAH",
        "zh-hant" to "HKD", "hu" to "HUF"
    )

    fun getDefaultCurrencyForLocale(context: Context): String? {
        val systemLang = getSystemLanguage()
        println("🌍 System language detected: $systemLang")

        val supportedLangs = context.resources.getStringArray(com.d4rk.android.libs.apptoolkit.R.array.preference_language_values)
        println("✅ Supported languages from resources: ${supportedLangs.toList()}")

        val matchedLang = supportedLangs.find { systemLang.startsWith(it) }
        println("🔍 Matched supported language: $matchedLang")

        val currencyHint = matchedLang?.let { languageToCurrencyCode[it] }
        println("💡 Currency hint from language map: $currencyHint")

        val availableCurrencies = context.resources.getStringArray(R.array.currency)
        println("💰 Available currencies from XML: ${availableCurrencies.toList()}")

        val matchedCurrency = currencyHint?.let { hint ->
            availableCurrencies.firstOrNull { it.startsWith(hint) }
        }

        println("🏁 Final matched currency: $matchedCurrency")
        return matchedCurrency
    }

    private fun getSystemLanguage(): String {
        val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            LocaleList.getDefault().get(0)
        } else {
            Locale.getDefault()
        }
        return locale.toLanguageTag().replace("-r", "-").lowercase()
    }
}
