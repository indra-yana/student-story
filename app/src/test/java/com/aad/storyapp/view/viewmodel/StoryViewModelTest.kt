package com.aad.storyapp.view.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aad.storyapp.BaseApplication
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.datasource.remote.response.StoryResponse
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.helper.MainDispatcherRule
import com.aad.storyapp.helper.getOrAwaitValue
import com.aad.storyapp.repository.StoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

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

    private var context: Application = mock(Application::class.java)
    private var baseApp: BaseApplication = mock(BaseApplication::class.java)
    private val storyViewModel: StoryViewModel = mock(StoryViewModel::class.java)
    private val storyRepository: StoryRepository = mock(StoryRepository::class.java)

    @Before
    fun setUp() {

    }

    @Test
    fun `When get stories should return story response with not empty list`() = runTest {
//        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyStoryResponse())
//
//        `when`(storyRepository.stories()).thenReturn(expectedResponse)
//        val actualResponse = storyRepository.stories()
//        Mockito.verify(storyRepository).stories()
//        Assert.assertNotNull(actualResponse)
//        Assert.assertTrue(actualResponse is ResponseStatus.Success)

        // Test to ensure method stories in storyViewModel is called
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
    }
}