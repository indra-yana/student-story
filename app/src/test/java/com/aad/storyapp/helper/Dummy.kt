package com.aad.storyapp.helper

import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.LoginResponse
import com.aad.storyapp.datasource.remote.response.StoryResponse
import com.aad.storyapp.model.Story
import com.aad.storyapp.model.User
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

/****************************************************
 * Created by Indra Muliana
 * On Friday, 07/10/2022 12.36
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

object Dummy {
    fun generateDummyStories(): ArrayList<Story> {
        val storyList = ArrayList<Story>()
        for (i in 0..10) {
            val story = Story(
                id = "story-$i",
                name = "john doe",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1665849088083_wh_K5qdK.jpg",
                description = "Lorem ipsum dolor sit amet.",
                createdAt = "2022-10-15T15:51:28.084Z",
                lat = null,
                lon = null
            )
            storyList.add(story)
        }

        return storyList
    }

    fun generateDummyStoryResponse(): StoryResponse {
        return StoryResponse(
            error = false,
            message = "Success!",
            listStory = generateDummyStories()
        )
    }

    fun generateDummyLoginResponse(isFailure: Boolean = false): LoginResponse {
        return LoginResponse(
            error = isFailure,
            message = if (isFailure) "Unauthenticated" else "Success!",
            loginResult = if (isFailure) null else User(
                id = "user-example-id",
                name = "User Example",
                token = "example.token.user"
            )
        )
    }

    fun generateDummyRegisterResponse(isFailure: Boolean = false): ApiResponse {
        return ApiResponse(
            error = isFailure,
            message = if (isFailure) "Register failure" else "Success!",
        )
    }

    fun generateDummyApiResponse(isFailure: Boolean = false): ApiResponse {
        return ApiResponse(
            error = isFailure,
            message = if (isFailure) "Failure" else "Success!",
        )
    }

    const val id = "user-fake-id"
    const val name = "Fake User"
    const val email = "fake@email.com"
    const val wrongEmail = "fakeinvalid@email.com"
    const val password = "fake_password"
    const val token = "kmz.way.87aa"

    val user = User(
        id = id,
        name = name,
        token = token
    )

    val emptyUser = User(
        id = "",
        name = "",
        token = ""
    )

    val photo = MultipartBody.Part.createFormData("fake_photo", "https://story-api.dicoding.dev/images/stories/photos-1666364446890_UU7bKvgX.jpg")
    val description = "fake description".toRequestBody("text/plain".toMediaType())
    val lat = "-6.128151".toRequestBody("text/plain".toMediaType())
    val lon = "106.844935".toRequestBody("text/plain".toMediaType())

    val fakeFailedLoginResponse = """
        {
          "error": true,
          "message": "Unauthenticated"
        }
    """.trimIndent().toResponseBody("text/plain".toMediaTypeOrNull())

    val fakeFailedRegisterResponse = """
        {
          "error": true,
          "message": "Register failure"
        }
    """.trimIndent().toResponseBody("text/plain".toMediaTypeOrNull())

    val loginFailureException = HttpException(Response.error<ResponseBody>(401, fakeFailedLoginResponse))
    val registerFailureException = HttpException(Response.error<ResponseBody>(400, fakeFailedRegisterResponse))

}