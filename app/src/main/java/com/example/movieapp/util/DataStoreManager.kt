package com.example.movieapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.movieapp.model.User
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


     suspend fun saveUser(user: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("user")] = user
        }
    }

    suspend fun logout() {
        saveUser("")
    }


    val userDetail: Flow<User?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("user")]?.let { json ->
                gson.fromJson(json, User::class.java)
            }
        }


}