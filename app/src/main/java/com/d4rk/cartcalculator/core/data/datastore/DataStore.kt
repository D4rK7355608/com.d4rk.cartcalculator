package com.d4rk.cartcalculator.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.d4rk.cartcalculator.core.utils.constants.datastore.AppDataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStore(context : Context) : CommonDataStore(context) {

    companion object {
        @Volatile
        private var instance : DataStore? = null

        fun getInstance(context : Context) : DataStore {
            return instance ?: synchronized(lock = this) {
                instance ?: DataStore(context.applicationContext).also { instance = it }
            }
        }
    }

    private val currencyKey : Preferences.Key<String> = stringPreferencesKey(name = AppDataStoreConstants.DATA_STORE_PREFERRED_CURRENCY)

    fun getCurrency() : Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[currencyKey] ?: ""
        }
    }

    suspend fun saveCurrency(currency : String) {
        dataStore.edit { preferences ->
            preferences[currencyKey] = currency
        }
    }

    private val openCartsAfterCreationKey : Preferences.Key<Boolean> = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_OPEN_CARTS_AFTER_CREATION)

    val openCartsAfterCreation : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[openCartsAfterCreationKey] ?: true
    }

    suspend fun saveOpenCartsAfterCreation(isEnabled : Boolean) {
        dataStore.edit { preferences ->
            preferences[openCartsAfterCreationKey] = isEnabled
        }
    }
}