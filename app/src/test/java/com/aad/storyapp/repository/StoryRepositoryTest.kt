package com.aad.storyapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.helper.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/****************************************************
 * Created by Indra Muliana
 * On Tuesday, 18/10/2022 21.50
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val storyRepository: StoryRepository = Mockito.mock(StoryRepository::class.java)

    @Test
    fun `When get stories should return story response with not empty list`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyStoryResponse())

        Mockito.`when`(storyRepository.stories()).thenReturn(expectedResponse)
        val actualResponse = storyRepository.stories()

        Mockito.verify(storyRepository).stories()
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResponseStatus.Success)
        Assert.assertTrue((actualResponse as ResponseStatus.Success).value.listStory.isNotEmpty())
    }

    @Test
    fun `When create stories should return response with success`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyApiResponse())

        val photo = Mockito.mock(MultipartBody.Part::class.java)
        val description = Mockito.mock(RequestBody::class.java)
        val lat = Mockito.mock(RequestBody::class.java)
        val lon = Mockito.mock(RequestBody::class.java)

        Mockito.`when`(storyRepository.create(photo, description, lat, lon)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.create(photo, description, lat, lon)

        Mockito.verify(storyRepository).create(photo, description, lat, lon)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResponseStatus.Success)
        Assert.assertEquals((actualResponse as ResponseStatus.Success).value.error, false)
    }
}