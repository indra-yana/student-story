package com.aad.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.LoginResponse
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.model.User

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 09.04
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class AuthRepository : BaseRepository() {

    suspend fun login(email: String, password: String): ResponseStatus<LoginResponse> {
        return safeApiCall(LoginResponse::class.java) {
            authApi.login(email, password)
        }
    }

    suspend fun register(name: String, email: String, password: String): ResponseStatus<ApiResponse> {
        return safeApiCall(ApiResponse::class.java) {
            authApi.register(name, email, password)
        }
    }

    suspend fun saveSession(user: User) {
        preferences.saveSession(user)
    }

    suspend fun destroySession() {
        preferences.destroySession()
    }

    fun getToken(): LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    fun getSession(): LiveData<User> {
        return preferences.getSession().asLiveData()
    }
}