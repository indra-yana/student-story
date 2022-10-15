package com.aad.storyapp.datasource.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 09/10/2022 11.06
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
