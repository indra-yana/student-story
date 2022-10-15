package com.aad.storyapp.repository

import androidx.paging.*
import androidx.room.withTransaction
import com.aad.storyapp.BaseApplication
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.entities.RemoteKeys
import com.aad.storyapp.datasource.remote.IStoryApi
import com.aad.storyapp.model.Story

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 09/10/2022 10.57
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator() : RemoteMediator<Int, Story>() {

    private val storyApi: IStoryApi by lazy { BaseApplication.storyApi }
    private val database: AppDatabase by lazy { BaseApplication.db }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val response = storyApi.stories(page, state.config.pageSize)
            val responseData = response.listStory
            val endOfPaginationReached = responseData.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                val storyEntities = responseData.map {
                    com.aad.storyapp.datasource.local.entities.Story(
                        id = it.id,
                        name = it.name,
                        photoUrl = it.photoUrl,
                        description = it.description,
                        createdAt = it.createdAt,
                        lat = it.lat,
                        lon = it.lon
                    )
                }

                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertStory(storyEntities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
}