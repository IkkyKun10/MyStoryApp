package com.riezki.storyapp.model.preference

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_NAME = "app_preference"

class DataStorePreference(private val dataStore: DataStore<Preferences>) {

    private object PreferenceKeys {
        val tokenPreferenceKey = stringPreferencesKey("token_preference_key")
        val loginStatePreferenceKey = booleanPreferencesKey("login_state_preference_key")
    }


    suspend fun saveTokenToDataStore(token: String) {
        dataStore.edit { mutablePreferences ->

            mutablePreferences[PreferenceKeys.tokenPreferenceKey] = token
        }
    }

    val readTokenFromDataStore: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { value ->
            val tokenUser = value[PreferenceKeys.tokenPreferenceKey] ?: "none"
            tokenUser
        }


    suspend fun saveLoginToDataStore(state: Boolean = true) {
        dataStore.edit { mutablePreferences ->

            mutablePreferences[PreferenceKeys.loginStatePreferenceKey] = state
        }
    }

    val readLoginStateFromDataStore: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { value ->
            val tokenUser = value[PreferenceKeys.loginStatePreferenceKey] ?: false
            tokenUser
        }

    suspend fun logoutFromDataStore() {
        dataStore.edit {
            it.clear()
        }
    }

}