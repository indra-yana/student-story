package com.aad.storyapp

import android.app.Application
import com.aad.storyapp.di.Injection.startDI

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 07.10
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startDI(this)
    }
}