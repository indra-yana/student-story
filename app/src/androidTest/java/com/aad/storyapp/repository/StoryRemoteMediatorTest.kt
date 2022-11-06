package com.aad.storyapp.repository

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.remote.IStoryApi
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.StoryResponse
import com.aad.storyapp.helper.Dummy
import com.aad.storyapp.model.Story
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/****************************************************
 * Created by Indra Muliana
 * On Friday, 21/10/2022 21.01
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: IStoryApi = FakeApiService()
    private var mockDb: AppDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        AppDatabase::class.java
    ).allowMainThreadQueries().build()

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(mockApi, mockDb)
        val pagingState = PagingState<Int, Story>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun whenCallCreateStoryShouldReturnSuccess() = runTest {
        val photo = MultipartBody.Part.createFormData("dummy_photo", "https://story-api.dicoding.dev/images/stories/photos-1666364446890_UU7bKvgX.jpg")
        val description = "dummyDesc".toRequestBody("text/plain".toMediaType())
        val lat = "-6.128151".toRequestBody("text/plain".toMediaType())
        val lon = "106.844935".toRequestBody("text/plain".toMediaType())

        val result = mockApi.create(photo, description, lat, lon)
        assertEquals(result.error, false)
    }
}

class FakeApiService : IStoryApi {
    override suspend fun create(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody): ApiResponse {
        return Dummy.generateDummyApiResponse()
    }

    override suspend fun stories(page: Int, size: Int, location: Int): StoryResponse {
        return Dummy.generateDummyStoryResponse()
    }
}