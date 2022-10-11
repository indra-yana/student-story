package com.aad.storyapp.datasource.remote.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApiResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
) : Parcelable