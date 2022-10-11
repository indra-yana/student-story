package com.aad.storyapp.datasource.remote.response


import android.os.Parcelable
import com.aad.storyapp.model.Story
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("listStory")
    val listStory: ArrayList<Story>,
    @SerializedName("message")
    val message: String
) : Parcelable