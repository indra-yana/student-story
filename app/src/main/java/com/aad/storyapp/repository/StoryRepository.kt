package com.aad.storyapp.repository

import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.datasource.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 09.22
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class StoryRepository : BaseRepository() {

    suspend fun create(photo: MultipartBody.Part, description: RequestBody): ResponseStatus<ApiResponse> {
        return safeApiCall(ApiResponse::class.java) {
            storyApi.create(photo, description)
        }
    }

    suspend fun createAsGuest(photo: MultipartBody.Part, description: RequestBody): ResponseStatus<ApiResponse> {
        return safeApiCall(ApiResponse::class.java) {
            storyApi.createAsGuest(photo, description)
        }
    }

    suspend fun stories(page: Int = 1, size: Int = 10, location: Int = 0): ResponseStatus<StoryResponse> {
        return safeApiCall(StoryResponse::class.java) {
            storyApi.stories(page, size, location)
        }
    }

    suspend fun saveStories(stories: String) {
        preferences.saveStories(stories)
    }

}