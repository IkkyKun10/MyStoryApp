package com.riezki.storyapp.network

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.*
import com.riezki.storyapp.model.local.AddNewStoryResultEntity
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.local.LoginResultEntity
import com.riezki.storyapp.model.local.RegisterResultEntity
import com.riezki.storyapp.network.api.ApiService
import com.riezki.storyapp.paging.data.StoryRemoteMediator
import com.riezki.storyapp.paging.database.StoryDatabase
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    fun getLoginUser(email: String, password: String): LiveData<Resource<LoginResultEntity>> {
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
                    Log.e("StoryRepository", "getLoginUser: ${e.message.toString()}")
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

    fun getRegisterUser(name: String, email: String, password: String): LiveData<Resource<RegisterResultEntity>> {
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
                    Log.e("StoryRepository", "getRegisterUser: ${e.message.toString()}")
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

    fun setUploadImage(
        context: Context,
        token: String,
        body: MultipartBody.Part,
        description: RequestBody,
        location: Location?
    ): LiveData<Resource<AddNewStoryResultEntity>> {
        return liveData {
            emit(Resource.Loading(null))
            wrapEspressoIdlingResource {
                try {
                    val paramsLoc = mutableMapOf<String, RequestBody>().apply {
                        put("description", description)
                        val lat = (location?.latitude ?: 0.0).toString().toRequestBody("text/plain".toMediaType())
                        val lng = (location?.longitude ?: 0.0).toString().toRequestBody("text/plain".toMediaType())
                        put("lat", lat)
                        put("lon", lng)
                    }

                    val response = apiService.uploadImage(token, body, HashMap(paramsLoc))
                    if (!response.error!!) {
                        val newAddStory = AddNewStoryResultEntity(
                            message = response.message
                        )
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                        emit(Resource.Success(newAddStory))
                    } else {
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(
                        Resource.Error(
                            statusCode = e.hashCode(),
                            message = e.message.toString(),
                            data = null
                        )
                    )
                    Log.e("TAG", "StoryRepository: setUploadImage")
                }
            }
        }
    }

    fun getMapStory(
        context: Context,
        token: String,
        page: Int
    ): LiveData<Resource<List<ItemListStoryEntity>?>> {
        return liveData {
            emit(Resource.Loading(null))
            wrapEspressoIdlingResource {
                try {
                    val response = apiService.getMapStory(token, page)
                    val listItem = response.listStory
                    if (response.error == true) {
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    } else {
                        val newList = listItem?.map {
                            ItemListStoryEntity(
                                name = it?.name ?: "",
                                description = it?.description ?: "",
                                idUser = it?.id ?: "",
                                lat = it?.lat ?: 0.0,
                                lon = it?.lon ?: 0.0
                            )
                        }

                        emit(Resource.Success(newList))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(
                        Resource.Error(
                            statusCode = e.hashCode(),
                            message = e.message.toString(),
                            data = null
                        )
                    )
                    Log.e("TAG", "StoryRepository: getMapStory")
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