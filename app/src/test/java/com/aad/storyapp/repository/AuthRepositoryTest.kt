package com.aad.storyapp.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.di.TestInjection
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.helper.Dummy.loginFailureException
import com.aad.storyapp.helper.Dummy.registerFailureException
import com.aad.storyapp.helper.MainDispatcherRule
import com.aad.storyapp.helper.getOrAwaitValue
import com.aad.storyapp.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

/****************************************************
 * Created by Indra Muliana
 * On Thursday, 20/10/2022 20.16
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest : KoinTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mock(Context::class.java)

    private val authRepository: AuthRepository by inject()

    private val user = Dummy.user

    @Before
    fun setUp() {
        TestInjection.startDI(context)
    }

    @After
    fun tearDown() {
        TestInjection.stopDI()
    }

    @Test
    fun `when login with correct credential should return response success`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyLoginResponse())
        val email = Dummy.email
        val password = Dummy.password

        val actualResponse = authRepository.login(email, password)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResponseStatus.Success)
        assertNotNull((actualResponse as ResponseStatus.Success).value.loginResult)
        assertEquals(actualResponse.value.error, false)
        assertEquals(actualResponse, expectedResponse)
    }

    @Test
    fun `when login with incorrect credential should return response failed`() = runTest {
        val expectedResponse = ResponseStatus.Failure(
            loginFailureException,
            Dummy.generateDummyLoginResponse(true)
        )
        val email = Dummy.wrongEmail
        val password = Dummy.password

        val actualResponse = authRepository.login(email, password)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResponseStatus.Failure)
        assertNull((actualResponse as ResponseStatus.Failure).value?.loginResult)
        assertEquals(actualResponse.value?.error, expectedResponse.value?.error)
    }

    @Test
    fun `when register with correct input should return response success`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyRegisterResponse())
        val name = Dummy.name
        val email = Dummy.email
        val password = Dummy.password

        val actualResponse = authRepository.register(name, email, password)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResponseStatus.Success)
        assertEquals((actualResponse as ResponseStatus.Success).value.error, false)
        assertEquals(actualResponse, expectedResponse)
    }

    @Test
    fun `when register with incorrect input should return response failure`() = runTest {
        val expectedResponse = ResponseStatus.Failure(
            registerFailureException,
            Dummy.generateDummyRegisterResponse(true)
        )
        val name = Dummy.name
        val email = Dummy.wrongEmail
        val password = Dummy.password

        val actualResponse = authRepository.register(name, email, password)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResponseStatus.Failure)
        assertEquals((actualResponse as ResponseStatus.Failure).value?.error, expectedResponse.value?.error)
    }

    @Test
    fun `when save session and then get token should return user token`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = user.token

        authRepository.saveSession(user)
        val actualValue = authRepository.getToken().getOrAwaitValue()

        assertNotNull(actualValue)
        assertEquals(actualValue, expectedToken.value)
    }

    @Test
    fun `when save session and then get session should return user`() = runTest {
        val expectedUser = MutableLiveData<User>()
        expectedUser.value = user

        authRepository.saveSession(user)
        val actualValue = authRepository.getSession().getOrAwaitValue()

        assertNotNull(actualValue)
        assertEquals(actualValue, expectedUser.value)
    }

    @Test
    fun `when destroy session should return user with empty value`() = runTest {
        val expectedUser = MutableLiveData<User>()
        expectedUser.value = Dummy.emptyUser

        authRepository.destroySession()
        val actualValue = authRepository.getSession().getOrAwaitValue()

        assertEquals(actualValue, expectedUser.value)
    }
}