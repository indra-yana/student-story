package com.aad.storyapp.di

import android.app.Application
import com.aad.storyapp.helper.appModuleTest
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 06/11/2022 19.26
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApplication)
            modules(
                appModuleTest,
                repositoryModule,
                viewModelModule
            )
        }
    }
}