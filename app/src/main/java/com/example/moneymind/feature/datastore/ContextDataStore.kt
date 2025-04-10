package com.example.moneymind.feature.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ContextDataStore(private val context: Context) {
    companion object {
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        private val NOTIFICATION_DAY = intPreferencesKey("notification_day")
    }

    val notificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_ENABLED] ?: false
        }

    val notificationDay: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_DAY] ?: 1 // dia padrão default como 1º
        }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun setNotificationDay(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_DAY] = day
        }
    }
}