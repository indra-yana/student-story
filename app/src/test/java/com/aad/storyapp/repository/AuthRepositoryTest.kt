package com.aad.storyapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.helper.MainDispatcherRule
import com.aad.storyapp.helper.getOrAwaitValue
import com.aad.storyapp.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/****************************************************
 * Created by Indra Muliana
 * On Thursday, 20/10/2022 20.16
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = Mockito.mock(AuthRepository::class.java)

    private val user = User(
        id = "user-example-id",
        name = "User Example",
        token = "example.token.user"
    )

    @Test
    fun `when login with correct credential should return response success`() = runTest {
        val email = "example@email.com"
        val password = "secret"

        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyLoginResponse())
        Mockito.`when`(authRepository.login(email, password)).thenReturn(expectedResponse)
        val actualResponse = authRepository.login(email, password)

        Mockito.verify(authRepository).login(email, password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResponseStatus.Success)
        Assert.assertNotNull((actualResponse as ResponseStatus.Success).value.loginResult)
        Assert.assertEquals(actualResponse.value.error, false)
    }

    @Test
    fun `when login with incorrect credential should return response failed`() = runTest {
        val email = "example@email.com"
        val password = "secret"

        val expectedResponse = ResponseStatus.Failure(Exception("Unauthenticated"), Dummy.generateDummyLoginResponse(true))
        Mockito.`when`(authRepository.login(email, password)).thenReturn(expectedResponse)
        val actualResponse = authRepository.login(email, password)

        Mockito.verify(authRepository).login(email, password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResponseStatus.Failure)
        Assert.assertNull((actualResponse as ResponseStatus.Failure).value?.loginResult)
        Assert.assertEquals(actualResponse.value?.error, true)
    }

    @Test
    fun `when register with correct input should return response success`() = runTest {
        val name = "example account"
        val email = "example@email.com"
        val password = "secret"

        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyRegisterResponse())
        Mockito.`when`(authRepository.register(name, email, password)).thenReturn(expectedResponse)
        val actualResponse = authRepository.register(name, email, password)

        Mockito.verify(authRepository).register(name, email, password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResponseStatus.Success)
        Assert.assertEquals((actualResponse as ResponseStatus.Success).value.error, false)
    }

    @Test
    fun `when register with incorrect input should return response failure`() = runTest {
        val name = "example account"
        val email = "a"
        val password = "2"

        val expectedResponse = ResponseStatus.Failure(Exception("Bad request!"), Dummy.generateDummyRegisterResponse(true))
        Mockito.`when`(authRepository.register(name, email, password)).thenReturn(expectedResponse)
        val actualResponse = authRepository.register(name, email, password)

        Mockito.verify(authRepository).register(name, email, password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResponseStatus.Failure)
        Assert.assertEquals((actualResponse as ResponseStatus.Failure).value?.error, true)
    }

    @Test
    fun `when save session and then get token should return user token`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = user.token

        // Ensure saveSession is called
        authRepository.saveSession(user)
        Mockito.verify(authRepository).saveSession(user)

        Mockito.`when`(authRepository.getToken()).thenReturn(expectedToken)
        val actualValue = authRepository.getToken().getOrAwaitValue()

        Mockito.verify(authRepository).getToken()
        Assert.assertNotNull(actualValue)
        Assert.assertEquals(actualValue, user.token)
    }

    @Test
    fun `when save session and then get session should return user`() = runTest {
        val expectedUser = MutableLiveData<User>()
        expectedUser.value = user

        // Ensure saveSession is called
        authRepository.saveSession(user)
        Mockito.verify(authRepository).saveSession(user)

        Mockito.`when`(authRepository.getSession()).thenReturn(expectedUser)
        val actualValue = authRepository.getSession().getOrAwaitValue()

        Mockito.verify(authRepository).getSession()
        Assert.assertNotNull(actualValue)
        Assert.assertEquals(actualValue, user)
    }

    @Test
    fun `when destroy session should return user with null value`() = runTest {
        val expectedUser = MutableLiveData<User>()
        expectedUser.value = null

        // Ensure saveSession is called
        authRepository.destroySession()
        Mockito.verify(authRepository).destroySession()

        Mockito.`when`(authRepository.getSession()).thenReturn(expectedUser)
        val actualValue = authRepository.getSession().getOrAwaitValue()

        Mockito.verify(authRepository).getSession()
        Assert.assertNull(actualValue)
    }
}