package com.riezki.storyapp.paging.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.network.api.ApiService
import com.riezki.storyapp.paging.database.RemoteKeys
import com.riezki.storyapp.paging.database.StoryDatabase

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val token: String,
    private val apiService: ApiService,
    private val database: StoryDatabase
) : RemoteMediator<Int, ItemListStoryEntity>() {

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, ItemListStoryEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKeys = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKeys
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKeys = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKeys
            }
        }

        try {
            val apiResponse = apiService.getListUser(token, page, state.config.pageSize)

            val endOfPaginationReached = apiResponse.listStory?.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached == true) null else page + 1
                val keys = apiResponse.listStory?.map {
                    RemoteKeys(id = it!!.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)

                val listItemStory = apiResponse.listStory?.map {
                    ItemListStoryEntity(
                        photoUrl = it?.photoUrl ?: "",
                        createdAt = it?.createdAt ?: "",
                        name = it?.name ?: "",
                        description = it?.description ?: "",
                        idUser = it?.id ?: ""
                    )
                }
                database.storyDao().insertStory(listItemStory)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("StoryRemoteMediator", "load: ${e.message.toString()} ")
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ItemListStoryEntity>) : RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.idUser)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ItemListStoryEntity>) : RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.idUser)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ItemListStoryEntity>) : RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.idUser?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

}