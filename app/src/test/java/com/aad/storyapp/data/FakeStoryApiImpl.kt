package com.aad.storyapp.data

import com.aad.storyapp.datasource.remote.IStoryApi
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.StoryResponse
import com.aad.storyapp.helper.Dummy
import okhttp3.MultipartBody
import okhttp3.RequestBody

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 20.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class FakeStoryApiImpl : IStoryApi {
    override suspend fun create(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody): ApiResponse {
        return Dummy.generateDummyApiResponse()
    }

    override suspend fun stories(page: Int, size: Int, location: Int): StoryResponse {
        return Dummy.generateDummyStoryResponse()
    }
}