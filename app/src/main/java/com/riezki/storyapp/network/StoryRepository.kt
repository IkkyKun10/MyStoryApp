package com.riezki.storyapp.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.paging.*
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.local.LoginResultEntity
import com.riezki.storyapp.model.local.RegisterResultEntity
import com.riezki.storyapp.network.api.ApiService
import com.riezki.storyapp.paging.data.StoryRemoteMediator
import com.riezki.storyapp.paging.database.StoryDatabase
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.wrapEspressoIdlingResource

class StoryRepository(private val apiService: ApiService, private val database: StoryDatabase) {

    fun getStoryUser(token: String): LiveData<Resource<PagingData<ItemListStoryEntity>>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(token, apiService, database),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            },
        ).liveData.map { pagingData ->
            listOf(Resource.Loading(data = null))
            Resource.Success(pagingData)
        }
    }

    fun getLoginUser(email: String, password: String) : LiveData<Resource<LoginResultEntity>> {
        return liveData {
            emit(Resource.Loading(null))
            wrapEspressoIdlingResource {
                try {
                    val response = apiService.loginUser(email, password)
                    val loginUser = response.loginResult
                    val newLoginUser = LoginResultEntity(
                        name = loginUser?.name ?: "",
                        token = loginUser?.token ?: "",
                        idUser = loginUser?.userId ?: ""
                    )

                    emit(Resource.Success(newLoginUser))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("StoryRepository","getLoginUser: ${e.message.toString()}")
                    emit(Resource.Error(
                        statusCode = e.hashCode(),
                        message = e.message.toString(),
                        data = null
                    ))
                }
            }
        }
    }

    fun getRegisterUser(name: String, email: String, password: String) : LiveData<Resource<RegisterResultEntity>> {
        return liveData {
            emit(Resource.Loading(null))
            wrapEspressoIdlingResource {
                try {
                    val param = mutableMapOf<String, String>()
                    param["name"] = name
                    param["email"] = email
                    param["password"] = password

                    val response = apiService.registerUser(param)
                    val registerUser = response.message
                    val newResponse = RegisterResultEntity(
                        message = registerUser ?: ""
                    )

                    emit(Resource.Success(newResponse))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("StoryRepository","getRegisterUser: ${e.message.toString()}")
                    emit(Resource.Error(
                        statusCode = e.hashCode(),
                        message = e.message.toString(),
                        data = null
                    ))
                }
            }
        }
    }



    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, database: StoryDatabase): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, database)
            }.also { instance = it }
        }
    }
}