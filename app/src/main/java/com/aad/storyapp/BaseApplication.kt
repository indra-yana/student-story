package com.aad.storyapp

import android.app.Application
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.datasource.local.dataStore
import com.aad.storyapp.datasource.remote.ApiClient
import com.aad.storyapp.datasource.remote.IAuthApi
import com.aad.storyapp.datasource.remote.IStoryApi
import com.aad.storyapp.repository.AuthRepository
import com.aad.storyapp.repository.StoryRepository

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 07.10
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        authApi = ApiClient.initApi(IAuthApi::class.java)
        storyApi = ApiClient.initApi(IStoryApi::class.java)
        pref = AppPreferences.initPreferences(dataStore)
        db = AppDatabase.getDatabase(applicationContext)
        authRepository = AuthRepository()
        storyRepository = StoryRepository()
    }

    companion object {
        @JvmStatic
        lateinit var authApi: IAuthApi

        @JvmStatic
        lateinit var storyApi: IStoryApi

        @JvmStatic
        lateinit var pref: AppPreferences

        @JvmStatic
        lateinit var db: AppDatabase

        @JvmStatic
        lateinit var authRepository: AuthRepository

        @JvmStatic
        lateinit var storyRepository: StoryRepository
    }
}