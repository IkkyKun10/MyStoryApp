package com.riezki.storyapp.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.riezki.storyapp.model.response.ListStoryItem
import com.riezki.storyapp.network.api.ApiService
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.wrapEspressoIdlingResource

class StoryRepository(private val apiService: ApiService) {

    fun getStoryUser(token: String): LiveData<Resource<List<ListStoryItem>?>> {
        return liveData {
            emit(Resource.Loading())
            wrapEspressoIdlingResource {
                try {
                    val response = apiService.getListUser(token)
                    val userStoryItem = response.listStory
                    val newStory = userStoryItem?.map { storyItem ->
                        ListStoryItem(
                            photoUrl = storyItem?.photoUrl ?: "",
                            createdAt = storyItem?.createdAt ?: "",
                            name = storyItem?.name ?: "",
                            description = storyItem?.description ?: "",
                            lat = storyItem?.lat ?: 0.0,
                            lon = storyItem?.lon ?: 0.0,
                            id = storyItem?.id ?: ""
                        )
                    }

                    emit(Resource.Success(newStory))
                } catch (e: Exception) {
                    Log.e("StoryRepository", "getStoryUser: ${e.message.toString()} ")
                    emit(
                        Resource.Error(
                            statusCode = e.hashCode(),
                            message = e.message.toString(),
                            data = null
                        )
                    )
                }
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
        }
    }
}