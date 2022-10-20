package com.aad.storyapp.view.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.LoginResponse
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.helper.MainDispatcherRule
import com.aad.storyapp.helper.getOrAwaitValue
import com.aad.storyapp.model.User
import com.aad.storyapp.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/****************************************************
 * Created by Indra Muliana
 * On Thursday, 20/10/2022 21.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authViewModel: AuthViewModel = Mockito.mock(AuthViewModel::class.java)
    private val authRepository: AuthRepository = Mockito.mock(AuthRepository::class.java)
    private val user = User(
        id = "user-example-id",
        name = "User Example",
        token = "example.token.user"
    )

    @Test
    fun `when user login should return success response with user`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyLoginResponse())
        val email = "example@email.com"
        val password = "secret"

        Mockito.`when`(authViewModel.login(email, password)).thenReturn(Job())
        authViewModel.login(email, password)
        Mockito.verify(authViewModel).login(email, password)

        val expectedUser = MutableLiveData<ResponseStatus<LoginResponse>>()
        expectedUser.value = ResponseStatus.Success(Dummy.generateDummyLoginResponse())

        Mockito.`when`(authViewModel.loginResponse).thenReturn(expectedUser)
        val actualStory = authViewModel.loginResponse.getOrAwaitValue()

        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is ResponseStatus.Success)
        Assert.assertEquals((actualStory as ResponseStatus.Success).value.error, false)
        Assert.assertEquals(actualStory.value.loginResult, expectedResponse.value.loginResult)
    }

    @Test
    fun `when user register should return success response`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyRegisterResponse())
        val name = "example account"
        val email = "example@email.com"
        val password = "secret"

        Mockito.`when`(authViewModel.register(name, email, password)).thenReturn(Job())
        authViewModel.register(name, email, password)
        Mockito.verify(authViewModel).register(name, email, password)

        val expectedRegister = MutableLiveData<ResponseStatus<ApiResponse>>()
        expectedRegister.value = ResponseStatus.Success(Dummy.generateDummyRegisterResponse())

        Mockito.`when`(authViewModel.registerResponse).thenReturn(expectedRegister)
        val actualStory = authViewModel.registerResponse.getOrAwaitValue()

        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is ResponseStatus.Success)
        Assert.assertEquals((actualStory as ResponseStatus.Success).value.error, false)
        Assert.assertEquals(actualStory.value.error, expectedResponse.value.error)
    }

    @Test
    fun `when get token should return token`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = user.token

        authViewModel.saveSession(user)
        Mockito.verify(authViewModel).saveSession(user)

        Mockito.`when`(authViewModel.token).thenReturn(expectedToken)
        val actualValue = authViewModel.token.getOrAwaitValue()

        Assert.assertNotNull(actualValue)
        Assert.assertEquals(actualValue, expectedToken.value)
    }

    @Test
    fun `when destroy session should return empty token`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = null

        authViewModel.destroySession()
        Mockito.verify(authViewModel).destroySession()

        Mockito.`when`(authViewModel.token).thenReturn(expectedToken)
        val actualValue = authViewModel.token.getOrAwaitValue()

        Assert.assertNull(actualValue)
        Assert.assertEquals(actualValue, expectedToken.value)
    }
}