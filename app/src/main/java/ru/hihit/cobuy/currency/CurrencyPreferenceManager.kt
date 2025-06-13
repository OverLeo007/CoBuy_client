package ru.hihit.cobuy.currency

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "currency_prefs"
private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)
private val KEY_SELECTED = stringPreferencesKey("selected_currency")

class CurrencyPreferenceManager(context: Context) {
    private val store = context.dataStore
    val selectedCurrencyFlow: Flow<String> = store.data
        .map { prefs -> prefs[KEY_SELECTED] ?: "RUB" }

    suspend fun setSelected(code: String) {
        store.edit { prefs -> prefs[KEY_SELECTED] = code }
    }
}