package com.aad.storyapp.view.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.aad.storyapp.data.FakeStoryPagingSource
import com.aad.storyapp.data.noopListUpdateCallback
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.di.TestInjection
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.helper.MainDispatcherRule
import com.aad.storyapp.helper.getOrAwaitValue
import com.aad.storyapp.model.Story
import com.aad.storyapp.repository.StoryRepository
import com.aad.storyapp.view.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
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
class StoryViewModelTest : KoinTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mock(Context::class.java)
    private val storyRepository: StoryRepository = mock(StoryRepository::class.java)

    private val storyViewModel: StoryViewModel by inject()

    @Before
    fun setUp() {
        TestInjection.startDI(context)
    }

    @After
    fun tearDown() {
        TestInjection.stopDI()
    }

    @Test
    fun `when get stories should return story response with not empty list`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyStoryResponse())

        `when`(storyRepository.stories()).thenReturn(expectedResponse)
        val actualResponse = storyRepository.stories()
        Mockito.verify(storyRepository).stories()

        storyViewModel.stories()
        val actualStory = storyViewModel.storiesResponse.getOrAwaitValue()

        assertNotNull(actualStory)
        assertTrue(actualStory is ResponseStatus.Success)
        assertTrue((actualStory as ResponseStatus.Success).value.listStory.isNotEmpty())
        assertEquals(actualStory.value.listStory, (actualResponse as ResponseStatus.Success).value.listStory)
    }

    @Test
    fun `when create stories should return response with success`() = runTest {
        val expectedResponse = ResponseStatus.Success(Dummy.generateDummyApiResponse())

        val photo = Dummy.photo
        val description = Dummy.description
        val lat = Dummy.lat
        val lon = Dummy.lon

        `when`(storyRepository.create(photo, description, lat, lon)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.create(photo, description, lat, lon)

        storyViewModel.create(photo, description, lat, lon)
        val actualStory = storyViewModel.storyCreateResponse.getOrAwaitValue()

        assertNotNull(actualStory)
        assertTrue(actualStory is ResponseStatus.Success)
        assertEquals((actualStory as ResponseStatus.Success).value.error, false)
        assertEquals(actualStory, actualResponse)
    }

    @Test
    fun `when get storiesWithPagination should return story response with not empty list`() = runTest {
        val dummyResponse = Dummy.generateDummyStoryResponse()
        val data: PagingData<Story> = FakeStoryPagingSource.snapshot(dummyResponse.listStory)
        val expectedStories = MutableLiveData<PagingData<Story>>()
        expectedStories.value = data

        `when`(storyRepository.storiesWithPagination()).thenReturn(expectedStories)
        val storyVM = StoryViewModel(storyRepository)
        val actualStories = storyVM.storiesResponsePager.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
            mainDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyResponse.listStory, differ.snapshot())
        assertEquals(dummyResponse.listStory.size, differ.snapshot().size)
        assertEquals(dummyResponse.listStory[0].name, differ.snapshot()[0]?.name)
    }
}