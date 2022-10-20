package com.aad.storyapp.datasource.remote.response


import android.os.Parcelable
import com.aad.storyapp.model.User
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("loginResult")
    val loginResult: User?,
    @SerializedName("message")
    val message: String
) : Parcelable