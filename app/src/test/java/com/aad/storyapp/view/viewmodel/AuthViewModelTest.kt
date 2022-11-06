package com.aad.storyapp.view.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.di.TestInjection
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.helper.MainDispatcherRule
import com.aad.storyapp.helper.getOrAwaitValue
import com.aad.storyapp.repository.AuthRepository
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
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

/****************************************************
 * Created by Indra Muliana
 * On Thursday, 20/10/2022 21.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest : KoinTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mock(Context::class.java)
    private val authRepository: AuthRepository = mock(AuthRepository::class.java)

    private val authViewModel: AuthViewModel by inject()

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
    fun `when user login should return success response with user`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyLoginResponse())
        val email = Dummy.email
        val password = Dummy.password

        Mockito.`when`(authRepository.login(email, password)).thenReturn(expectedResponse)
        val actualResponse = authRepository.login(email, password)
        Mockito.verify(authRepository).login(email, password)

        authViewModel.login(email, password)
        val actualLogin = authViewModel.loginResponse.getOrAwaitValue()

        assertNotNull(actualLogin)
        assertTrue(actualLogin is ResponseStatus.Success)
        assertEquals((actualLogin as ResponseStatus.Success).value.error, false)
        assertEquals(actualLogin.value.loginResult, expectedResponse.value.loginResult)
        assertEquals(actualLogin, actualResponse)
    }

    @Test
    fun `when user register should return success response`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyRegisterResponse())
        val name = Dummy.name
        val email = Dummy.email
        val password = Dummy.password

        Mockito.`when`(authRepository.register(name, email, password)).thenReturn(expectedResponse)
        val actualResponse = authRepository.register(name, email, password)
        Mockito.verify(authRepository).register(name, email, password)

        authViewModel.register(name, email, password)
        val actualRegister = authViewModel.registerResponse.getOrAwaitValue()

        assertNotNull(actualRegister)
        assertTrue(actualRegister is ResponseStatus.Success)
        assertEquals((actualRegister as ResponseStatus.Success).value.error, false)
        assertEquals(actualRegister.value.error, expectedResponse.value.error)
        assertEquals(actualRegister, actualResponse)
    }

    @Test
    fun `when save session then get token should return token`() = runTest {
        val expectedToken = user.token

        authViewModel.saveSession(user)
        val actualToken = authViewModel.token.getOrAwaitValue()

        assertNotNull(actualToken)
        assertEquals(actualToken, expectedToken)
    }

    @Test
    fun `when destroy session should return empty token`() = runTest {
        val expectedToken = ""

        authViewModel.destroySession()
        val actualToken = authViewModel.token.getOrAwaitValue()

        assertEquals(actualToken, expectedToken)
    }
}