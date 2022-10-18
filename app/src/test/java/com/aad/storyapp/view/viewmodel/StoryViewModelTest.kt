package com.aad.storyapp.view.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.datasource.remote.response.StoryResponse
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.helper.MainDispatcherRule
import com.aad.storyapp.helper.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 15/10/2022 23.36
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val storyViewModel: StoryViewModel = mock(StoryViewModel::class.java)

    @Test
    fun `When get stories should return story response with not empty list`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyStoryResponse())

        `when`(storyViewModel.stories()).thenReturn(Job())
        storyViewModel.stories()
        Mockito.verify(storyViewModel).stories()

        val expectedStory = MutableLiveData<ResponseStatus<StoryResponse>>()
        expectedStory.value = ResponseStatus.Success(Dummy.generateDummyStoryResponse())

        `when`(storyViewModel.storiesResponse).thenReturn(expectedStory)
        val actualStory = storyViewModel.storiesResponse.getOrAwaitValue()

        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is ResponseStatus.Success)
        Assert.assertTrue((actualStory as ResponseStatus.Success).value.listStory.isNotEmpty())
        Assert.assertEquals(actualStory.value.listStory, expectedResponse.value.listStory)
    }

    @Test
    fun `When create stories should return response with success`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyApiResponse())

        val photo = mock(MultipartBody.Part::class.java)
        val description = mock(RequestBody::class.java)
        val lat = mock(RequestBody::class.java)
        val lon = mock(RequestBody::class.java)

        // Test to ensure method stories in storyViewModel is called
        `when`(storyViewModel.create(photo, description, lat, lon)).thenReturn(Job())
        storyViewModel.create(photo, description, lat, lon)
        Mockito.verify(storyViewModel).create(photo, description, lat, lon)

        val expectedCreateResponse = MutableLiveData<ResponseStatus<ApiResponse>>()
        expectedCreateResponse.value = ResponseStatus.Success(Dummy.generateDummyApiResponse())

        `when`(storyViewModel.storyCreateResponse).thenReturn(expectedCreateResponse)
        val actualCreateResponse = storyViewModel.storyCreateResponse.getOrAwaitValue()

        Assert.assertNotNull(actualCreateResponse)
        Assert.assertTrue(actualCreateResponse is ResponseStatus.Success)
        Assert.assertEquals((actualCreateResponse as ResponseStatus.Success).value.error, false)
        Assert.assertEquals(actualCreateResponse, expectedResponse)
    }
}