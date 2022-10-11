package com.aad.storyapp.datasource.remote

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import com.aad.storyapp.BaseApplication
import com.aad.storyapp.BuildConfig
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.helper.Constant
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/****************************************************
 * Created by Indra Muliana
 * On Friday, 23/09/2022 22.09
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

object ApiClient {

    private val retrofit: Retrofit
    private val preferences: AppPreferences by lazy { BaseApplication.pref }

    init {
        // Add interceptors to add query param or some header
        val requestInterceptor = Interceptor { chain ->
            /*
            // URL Builder Example
            val urlBuilder = chain.request()
                .url
                .newBuilder()
                .addQueryParameter("api_key", "API_KEY")
                .build()
             */

            var token: String
            runBlocking {
                token = preferences.getToken().first()
            }

            // Request Header Example
            val requestHeaders = chain.request()
                .newBuilder()
                // .url(urlBuilder)
                .addHeader("Accept", "application/json")
                .apply {
                    if (token.isNotEmpty()) {
                        addHeader("Authorization", "bearer $token")
                    }
                }
                .build()

            return@Interceptor chain.proceed(requestHeaders)
        }

        // Add logging interceptor if needed
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        )

        // OkHttpClient
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(loggingInterceptor)
                }
            }
            .connectTimeout(Constant.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <Api> initApi(api: Class<Api>): Api = retrofit.create(api)
}