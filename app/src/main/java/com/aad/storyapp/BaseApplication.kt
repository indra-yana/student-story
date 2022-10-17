package com.aad.storyapp

import android.app.Application
import com.aad.storyapp.di.appModule
import com.aad.storyapp.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 07.10
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

//        authApi = ApiClient.initApi(IAuthApi::class.java)
//        storyApi = ApiClient.initApi(IStoryApi::class.java)
//        pref = AppPreferences(this).initPreferences(this)
//        db = AppDatabase.getDatabase(this)
//        authRepository = AuthRepository()
//        storyRepository = StoryRepository()
    }

    init {
        startKoin {
            androidContext(this@BaseApplication)
            modules(
                appModule,
                repositoryModule
            )
        }
    }

//    companion object {
//        @JvmStatic
//        lateinit var authApi: IAuthApi
//
//        @JvmStatic
//        lateinit var storyApi: IStoryApi
//
//        @JvmStatic
//        lateinit var pref: AppPreferences
//
//        @JvmStatic
//        lateinit var db: AppDatabase
//
//        @JvmStatic
//        lateinit var authRepository: AuthRepository
//
//        @JvmStatic
//        lateinit var storyRepository: StoryRepository
//    }
}