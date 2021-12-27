package com.example.preferences.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.preferences.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


const val USER_DATA_KEY = "user_key" // Key for the DataStore UserPreferences
val NAME_KEY = stringPreferencesKey("name_key")
val LAST_NAME_ONE_KEY = stringPreferencesKey("last_name_one_key")
val LAST_NAME_TWO_KEY = stringPreferencesKey("last_name_two_key")

class UserPreferences(private val context: Application) {

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(USER_DATA_KEY)

    val user: Flow<User> = context.datastore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val name: String = preferences[NAME_KEY] ?: ""
            val lastNameOne: String = preferences[LAST_NAME_ONE_KEY] ?: ""
            val lastNameTwo: String = preferences[LAST_NAME_TWO_KEY] ?: ""

            User(name, lastNameOne, lastNameTwo)
        }

    suspend fun setUserData(name: String, lastNameOne: String, lastNameTwo: String) {
        context.datastore.edit { preferences->
            preferences[NAME_KEY] = name
            preferences[LAST_NAME_ONE_KEY] = lastNameOne
            preferences[LAST_NAME_TWO_KEY] = lastNameTwo
        }
    }
}