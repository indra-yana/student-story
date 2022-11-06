package com.aad.storyapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.LoginResponse
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.model.User
import com.aad.storyapp.repository.AuthRepository
import kotlinx.coroutines.launch

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 09.36
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResponse: MutableLiveData<ResponseStatus<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<ResponseStatus<LoginResponse>> get() = _loginResponse

    private val _registerResponse: MutableLiveData<ResponseStatus<ApiResponse>> = MutableLiveData()
    val registerResponse: LiveData<ResponseStatus<ApiResponse>> get() = _registerResponse

    val token: LiveData<String> get() = authRepository.getToken()
    val session: LiveData<User> get() = authRepository.getSession()

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginResponse.value = ResponseStatus.Loading
        _loginResponse.value = authRepository.login(email, password)
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _registerResponse.value = ResponseStatus.Loading
        _registerResponse.value = authRepository.register(name, email, password)
    }

    suspend fun saveSession(user: User) {
        authRepository.saveSession(user)
    }

    fun destroySession() = viewModelScope.launch {
        authRepository.destroySession()
    }

}