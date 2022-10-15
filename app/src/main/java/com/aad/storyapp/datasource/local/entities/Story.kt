package com.aad.storyapp.datasource.local.entities


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/****************************************************
 * Created by Indra Muliana
 * On Friday, 23/09/2022 21.36
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

@Parcelize
@Entity(tableName = "stories")
data class Story(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lon")
    val lon: Double? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("photoUrl")
    val photoUrl: String
) : Parcelable