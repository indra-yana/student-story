package com.aad.storyapp.di

import android.content.Context
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.datasource.local.dataStore
import com.aad.storyapp.datasource.remote.ApiClient
import com.aad.storyapp.datasource.remote.IAuthApi
import com.aad.storyapp.datasource.remote.IStoryApi
import com.aad.storyapp.repository.AuthRepository
import com.aad.storyapp.repository.StoryRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 20.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

fun provideAuthApi(): IAuthApi {
    return ApiClient.initApi(IAuthApi::class.java)
}

fun provideStoryApi(): IStoryApi {
    return ApiClient.initApi(IStoryApi::class.java)
}

fun provideStoryRepository(): StoryRepository {
    return StoryRepository()
}

fun provideAuthRepository(): AuthRepository {
    return AuthRepository()
}

fun providePreferences(context: Context): AppPreferences {
    return AppPreferences.initPreferences(context.dataStore)
}

fun provideDatabase(context: Context): AppDatabase {
    return AppDatabase.getDatabase(context.applicationContext)
}

val appModule = module {
    single { provideAuthApi() }
    single { provideStoryApi() }
    single { provideStoryRepository() }
    single { provideAuthRepository() }
    factory { providePreferences(androidContext()) }
    factory { provideDatabase(androidContext()) }
}