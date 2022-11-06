package com.aad.storyapp.di

import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.datasource.remote.IAuthApi
import com.aad.storyapp.datasource.remote.IStoryApi
import com.aad.storyapp.repository.AuthRepository
import com.aad.storyapp.repository.StoryRepository
import org.koin.dsl.module

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 20.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

fun provideStoryRepository(
    storyApi: IStoryApi,
    database: AppDatabase,
    preferences: AppPreferences
): StoryRepository {
    return StoryRepository(storyApi, database, preferences)
}

fun provideAuthRepository(
    authApi: IAuthApi,
    preferences: AppPreferences
): AuthRepository {
    return AuthRepository(authApi, preferences)
}

val repositoryModule = module {
    single { provideStoryRepository(get(), get(), get()) }
    single { provideAuthRepository(get(), get()) }
}