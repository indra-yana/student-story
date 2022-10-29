package com.aad.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aad.storyapp.datasource.local.dao.StoryDao
import com.aad.storyapp.datasource.local.entities.Story
import com.aad.storyapp.helper.DataClassMapper

/****************************************************
 * Created by Indra Muliana
 * On Friday, 28/10/2022 18.59
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class FakeStoryDaoImpl : StoryDao {

    private val stories: ArrayList<Story> = arrayListOf()

    override suspend fun insertStory(stories: List<Story>) {
        this.stories.addAll(stories)
    }

    override fun getAllStory(): PagingSource<Int, com.aad.storyapp.model.Story> {
        return FakePagingSource(DataClassMapper.mapStoryEntityToStoryModel(stories))
    }

    override fun getAllStoryAsList(limit: Int, offset: Int): List<Story> {
        return stories
    }

    override suspend fun deleteAll() {
        stories.clear()
    }
}

class FakePagingSource(private val data: List<com.aad.storyapp.model.Story>) : PagingSource<Int, com.aad.storyapp.model.Story>() {
    @Suppress("SameReturnValue")
    override fun getRefreshKey(state: PagingState<Int, com.aad.storyapp.model.Story>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, com.aad.storyapp.model.Story> {
        return LoadResult.Page(data, 0, 1)
    }
}
