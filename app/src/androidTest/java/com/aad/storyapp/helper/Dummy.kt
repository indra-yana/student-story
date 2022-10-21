package com.aad.storyapp.helper

import com.aad.storyapp.datasource.local.entities.Story
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.StoryResponse

/****************************************************
 * Created by Indra Muliana
 * On Friday, 07/10/2022 12.36
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

object Dummy {
    fun generateDummyStoriesEntity(): ArrayList<Story> {
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

    fun generateDummyStories(): ArrayList<com.aad.storyapp.model.Story> {
        val storyList = ArrayList<com.aad.storyapp.model.Story>()
        for (i in 0..10) {
            val story = com.aad.storyapp.model.Story(
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

    fun generateDummyApiResponse(): ApiResponse {
        return ApiResponse(
            error = false,
            message = "Success!",
        )
    }
}