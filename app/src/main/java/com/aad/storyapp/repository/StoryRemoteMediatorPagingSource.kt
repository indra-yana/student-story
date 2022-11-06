package com.aad.storyapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.helper.DataClassMapper.mapStoryEntityToStoryModel
import com.aad.storyapp.model.Story

/****************************************************
 * Created by Indra Muliana
 * On Friday, 14/10/2022 18.59
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class StoryRemoteMediatorPagingSource(private val database: AppDatabase) : PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 0
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            // position=page
            val position = params.key ?: INITIAL_PAGE_INDEX

            database.withTransaction {
                val responseData = database.storyDao().getAllStoryAsList(params.loadSize, position * params.loadSize)
                val list = mapStoryEntityToStoryModel(responseData)

                LoadResult.Page(
                    data = list,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (list.isEmpty()) null else position + 1
                )
            }
        } catch (ex: Exception) {
            return LoadResult.Error(ex)
        }
    }
}