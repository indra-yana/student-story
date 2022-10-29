package com.aad.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aad.storyapp.data.FakeAppDatabase
import com.aad.storyapp.data.FakeAuthApiImpl
import com.aad.storyapp.data.FakePreferences
import com.aad.storyapp.data.FakeStoryApiImpl
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.datasource.remote.IAuthApi
import com.aad.storyapp.datasource.remote.IStoryApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun provideAuthApi(): IAuthApi {
    return FakeAuthApiImpl()
}

fun provideStoryApi(): IStoryApi {
    return FakeStoryApiImpl()
}

fun providePreferences(context: Context): AppPreferences {
    return FakePreferences(context)
}

fun provideDatabase(context: Context): AppDatabase {
    return FakeAppDatabase()
}

fun providePreferencesDataStore(appContext: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        migrations = listOf(SharedPreferencesMigration(appContext, "setting")),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { appContext.preferencesDataStoreFile("setting") }
    )
}


val appModuleTest = module {
    single { provideAuthApi() }
    single { provideStoryApi() }
    single { providePreferences(androidContext()) }
    single { provideDatabase(androidContext()) }
    single { providePreferencesDataStore(androidContext()) }
}