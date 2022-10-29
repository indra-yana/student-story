package com.aad.storyapp.data

import com.aad.storyapp.datasource.remote.IAuthApi
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.LoginResponse
import com.aad.storyapp.helper.Dummy

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 20.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class FakeAuthApiImpl : IAuthApi {
    override suspend fun login(email: String, password: String): LoginResponse {
        if (email != Dummy.email) {
            throw Dummy.loginFailureException
        }

        return Dummy.generateDummyLoginResponse(email != Dummy.email)
    }

    override suspend fun register(name: String, email: String, password: String): ApiResponse {
        if (email != Dummy.email) {
            throw Dummy.registerFailureException
        }

        return Dummy.generateDummyRegisterResponse(email != Dummy.email)
    }
}