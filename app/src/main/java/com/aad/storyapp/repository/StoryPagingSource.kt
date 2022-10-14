package com.aad.storyapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aad.storyapp.BaseApplication
import com.aad.storyapp.datasource.remote.IStoryApi
import com.aad.storyapp.model.Story
import java.lang.Exception

/****************************************************
 * Created by Indra Muliana
 * On Friday, 14/10/2022 18.59
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class StoryPagingSource : PagingSource<Int, Story>() {

    private val storyApi: IStoryApi by lazy { BaseApplication.storyApi }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = storyApi.stories(position, params.loadSize, 0)
            val list = responseData.listStory

            LoadResult.Page(
                data = list,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (list.isEmpty()) null else position + 1
            )
        } catch (ex: Exception) {
            return LoadResult.Error(ex)
        }
    }
}