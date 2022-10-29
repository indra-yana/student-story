package com.aad.storyapp.data

import android.content.Context
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 20.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class FakePreferences(context: Context) : AppPreferences(context) {

    private lateinit var user: User
    private lateinit var token: String

    override suspend fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): Flow<String> {
        return flow {
            emit(token)
        }
    }

    override suspend fun saveSession(user: User) {
        this.user = user

        saveToken(user.token)
    }

    override fun getSession(): Flow<User> {
        return flow {
            emit(user)
        }
    }

    override suspend fun destroySession() {
        user = Dummy.emptyUser
        token = ""
    }
}