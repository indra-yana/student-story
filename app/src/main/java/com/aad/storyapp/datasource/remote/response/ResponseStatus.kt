package com.aad.storyapp.datasource.remote.response

import okhttp3.ResponseBody

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 07.27
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

@Suppress("unused")
sealed class ResponseStatus<out T> {
    object Loading : ResponseStatus<Nothing>()
    data class Success<out T>(val value: T) : ResponseStatus<T>()
    data class Failure<out T>(val exception: Exception, val value: T? = null) : ResponseStatus<T>()
    data class Error(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : ResponseStatus<Nothing>()
}