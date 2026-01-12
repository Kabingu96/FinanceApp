package com.example.financeapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {
    
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    private val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
    private val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
    private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: false // Default to false (System default logic can be added)
        }
        
    val useDynamicColors: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USE_DYNAMIC_COLORS] ?: true
        }

    val currencySymbol: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENCY_SYMBOL] ?: "$"
        }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[BIOMETRIC_ENABLED] ?: false
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = enabled
        }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_DYNAMIC_COLORS] = enabled
        }
    }

    suspend fun setCurrencySymbol(symbol: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_SYMBOL] = symbol
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED] = enabled
        }
    }
}
