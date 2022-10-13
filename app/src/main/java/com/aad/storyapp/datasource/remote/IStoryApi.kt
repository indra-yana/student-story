package com.aad.storyapp.datasource.remote

import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/****************************************************
 * Created by Indra Muliana
 * On Friday, 23/09/2022 22.39
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

interface IStoryApi {
    @Multipart
    @POST("stories")
    suspend fun create(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): ApiResponse

    @Multipart
    @POST("stories/guest")
    suspend fun createAsGuest(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): ApiResponse

    @GET("stories")
    suspend fun stories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ): StoryResponse
}