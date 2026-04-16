package com.ciberssh.liki.utils

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

object PreferencesManager {
    private val IS_ADMIN = booleanPreferencesKey("is_admin")
    private val CURRENT_VERSION = stringPreferencesKey("current_version")
    private val UPDATE_DISMISSED = booleanPreferencesKey("update_dismissed")

    fun isAdmin(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_ADMIN] ?: false
        }
    }

    suspend fun setAdmin(context: Context, isAdmin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ADMIN] = isAdmin
        }
    }

    fun getCurrentVersion(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[CURRENT_VERSION] ?: "1.0.0"
        }
    }

    suspend fun setCurrentVersion(context: Context, version: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_VERSION] = version
        }
    }

    fun isUpdateDismissed(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[UPDATE_DISMISSED] ?: false
        }
    }

    suspend fun setUpdateDismissed(context: Context, dismissed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UPDATE_DISMISSED] = dismissed
        }
    }
}
