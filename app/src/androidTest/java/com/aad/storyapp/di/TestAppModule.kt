package com.aad.storyapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.datasource.remote.ApiClient
import com.aad.storyapp.datasource.remote.IAuthApi
import com.aad.storyapp.datasource.remote.IStoryApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 20.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

fun provideApiClient(): ApiClient {
    return ApiClient(Constant.TEST_BASE_URL)
}

fun provideAuthApi(apiClient: ApiClient): IAuthApi {
    return apiClient.initApi(IAuthApi::class.java)
}

fun provideStoryApi(apiClient: ApiClient): IStoryApi {
    return apiClient.initApi(IStoryApi::class.java)
}

fun providePreferences(context: Context): AppPreferences {
    return AppPreferences(context)
}

fun provideDatabase(context: Context): AppDatabase {
    return AppDatabase.getDatabase(context.applicationContext)
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
    single { provideApiClient() }
    single { provideAuthApi(get()) }
    single { provideStoryApi(get()) }
    single { providePreferences(androidContext()) }
    single { provideDatabase(androidContext()) }
    single { providePreferencesDataStore(androidContext()) }
}