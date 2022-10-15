package com.aad.storyapp.repository

import com.aad.storyapp.BaseApplication
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.datasource.remote.IAuthApi
import com.aad.storyapp.datasource.remote.IStoryApi
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.errorBodyConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 07.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

abstract class BaseRepository {

    protected val authApi: IAuthApi by lazy { BaseApplication.authApi }
    protected val storyApi: IStoryApi by lazy { BaseApplication.storyApi }
    protected val preferences: AppPreferences by lazy { BaseApplication.pref }
    protected val database: AppDatabase by lazy { BaseApplication.db }

    suspend fun <T> safeApiCall(clazz: Class<T>, apiCall: suspend () -> T): ResponseStatus<T> {
        return withContext(Dispatchers.IO) {
            try {
                ResponseStatus.Success(apiCall.invoke())
            } catch (exception: Exception) {
                when (exception) {
                    is HttpException -> {
                        ResponseStatus.Failure(exception, errorBodyConverter(clazz, exception.response()?.errorBody()?.string()))
                    }
                    else -> ResponseStatus.Failure(exception)
                }
            }
        }
    }

}