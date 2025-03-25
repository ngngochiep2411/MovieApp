package com.example.movieapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.movieapp.model.UserDetail
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("user_preferences")

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {


    private var dataStore: DataStore<Preferences> = context.dataStore


    private val keyUserData = stringPreferencesKey("userData")


    val userName: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[keyUserData]
        }
    val isLogin: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[booleanPreferencesKey("isLogin")] == true
        }


    val gson = Gson()

    suspend fun saveUserDetail(userDetail: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("userData")] = userDetail
        }
    }

    suspend fun logout() {
        saveUserDetail("")
    }

    val userDetail: Flow<UserDetail?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("userData")]?.let { json ->
                gson.fromJson(json, UserDetail::class.java)
            }
        }


}