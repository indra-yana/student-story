package com.aad.storyapp.datasource.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aad.storyapp.datasource.local.entities.Story

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 09/10/2022 10.53
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<Story>)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, com.aad.storyapp.model.Story>

    @Query("SELECT * FROM stories ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getAllStoryAsList(limit: Int, offset: Int): List<Story>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()

}