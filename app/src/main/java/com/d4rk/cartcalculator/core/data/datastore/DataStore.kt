package com.d4rk.cartcalculator.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.d4rk.cartcalculator.app.cart.list.domain.model.SortOption
import com.d4rk.cartcalculator.core.utils.constants.datastore.AppDataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStore(val context : Context) : CommonDataStore(context = context) {

    private val hasSeenConfettiKey : Preferences.Key<Boolean> = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_HAS_SEEN_CONFETTI)

    fun hasSeenConfetti(): Flow<Boolean> {
        return dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs -> prefs[hasSeenConfettiKey] == true }
    }

    suspend fun saveHasSeenConfetti(seen : Boolean) {
        dataStore.edit { preferences : MutablePreferences ->
            preferences[hasSeenConfettiKey] = seen
        }
    }

    private val currencyKey : Preferences.Key<String> = stringPreferencesKey(name = AppDataStoreConstants.DATA_STORE_PREFERRED_CURRENCY)

    fun getCurrency() : Flow<String> = dataStore.data.map { preferences : Preferences ->
        preferences[currencyKey] ?: ""
    }

    suspend fun saveCurrency(currency : String) {
        dataStore.edit { preferences : MutablePreferences ->
            preferences[currencyKey] = currency
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

    private val lastBackupDateKey: Preferences.Key<String> =
        stringPreferencesKey(name = AppDataStoreConstants.DATA_STORE_LAST_BACKUP_DATE)

    val lastBackupDateFlow: Flow<String?> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }
        .map { preferences: Preferences ->
            preferences[lastBackupDateKey]
        }

    suspend fun saveLastBackupDate(dateString: String?) {
        dataStore.edit { preferences: MutablePreferences ->
            if (dateString != null) {
                preferences[lastBackupDateKey] = dateString
            } else {
                preferences.remove(lastBackupDateKey)
            }
        }
    }

    private val sortOptionKey: Preferences.Key<String> =
        stringPreferencesKey(name = AppDataStoreConstants.DATA_STORE_SORT_OPTION)

    val sortOption: Flow<SortOption> = dataStore.data.map { preferences: Preferences ->
        val saved = preferences[sortOptionKey]
        runCatching { SortOption.valueOf(saved ?: SortOption.DEFAULT.name) }.getOrDefault(SortOption.DEFAULT)
    }

    suspend fun saveSortOption(option: SortOption) {
        dataStore.edit { preferences: MutablePreferences ->
            preferences[sortOptionKey] = option.name
        }
    }
}