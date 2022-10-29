package com.aad.storyapp.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/****************************************************
 * Created by Indra Muliana
 * On Thursday, 27/10/2022 20.09
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

object Injection {
    fun startDI(context: Context) {
        startKoin {
            androidContext(context)
            modules(
                appModule,
                repositoryModule,
                viewModelModule
            )
        }
    }

    fun stopDI() {
        stopKoin()
    }
}