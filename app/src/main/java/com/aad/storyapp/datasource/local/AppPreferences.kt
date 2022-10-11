package com.aad.storyapp.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aad.storyapp.model.Story
import com.aad.storyapp.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/****************************************************
 * Created by Indra Muliana
 * On Friday, 23/09/2022 21.13
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: User) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.id
            preferences[NAME_KEY] = user.name
        }

        saveToken(user.token)
    }

    suspend fun destroySession() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN)
            preferences.remove(ID_KEY)
            preferences.remove(NAME_KEY)
        }
    }

    private suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun saveStories(stories: String) {
        dataStore.edit { preferences ->
            preferences[STORIES_KEY] = stories
        }
    }

    fun getStories(): Flow<ArrayList<Story>?> {
        return dataStore.data.map { preferences ->
            if (preferences[STORIES_KEY] != null) {
                val arrayListTutorialType = object : TypeToken<ArrayList<Story>>() {}.type
                return@map Gson().fromJson<ArrayList<Story>?>(preferences[STORIES_KEY], arrayListTutorialType)
            }

            return@map null
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }

    fun getSession(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[ID_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN] ?: ""
            )
        }
    }

    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val ID_KEY = stringPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val STORIES_KEY = stringPreferencesKey("stories")

        @Volatile
        private var INSTANCE: AppPreferences? = null

        @Synchronized
        fun initPreferences(dataStore: DataStore<Preferences>): AppPreferences {
            return INSTANCE ?: AppPreferences(dataStore).also { INSTANCE = it }
        }
    }

}