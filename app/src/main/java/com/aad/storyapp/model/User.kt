package com.aad.storyapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/****************************************************
 * Created by Indra Muliana
 * On Friday, 23/09/2022 21.36
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

@Parcelize
data class User(
    @SerializedName("userId")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("token")
    val token: String
) : Parcelable
