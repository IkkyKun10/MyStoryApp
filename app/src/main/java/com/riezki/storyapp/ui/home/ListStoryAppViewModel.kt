package com.riezki.storyapp.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.model.response.ListStoryResponse
import com.riezki.storyapp.network.api.ApiConfig
import com.riezki.storyapp.utils.ErrorCode
import com.riezki.storyapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ListStoryAppViewModel(private val dataStore: DataStorePreference) : ViewModel() {

    companion object {
        private const val TAG = "ListStoryViewModel"
    }

    val userTokenFromDataStore = dataStore.readTokenFromDataStore.asLiveData()

    private var result = MutableLiveData<Resource<List<ItemListStoryEntity>>>()

    fun setListStory(token: String) {
        val apiService = ApiConfig().getApiService()

        apiService.getListUser(token).enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(call: Call<ListStoryResponse>, response: Response<ListStoryResponse>) {
                val responseBody = response.body()?.listStory

                if (response.isSuccessful) {
                    if (responseBody != null) {
                        result.postValue(
                            Resource.Success(
                                responseBody.map {
                                    ItemListStoryEntity(
                                        photoUrl = it?.photoUrl ?: "",
                                        createdAt = it?.createdAt ?: "",
                                        name = it?.name ?: "",
                                        description = it?.description ?: "",
                                        idUser = it?.id ?: ""
                                    )
                                }
                            )
                        )
                    }
                }
                else {
                    result.value = Resource.Error(response.code(), response.message(), null)
                    Log.e(TAG, response.message())
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                when (t) {
                    is UnknownHostException ->{
                        result.value =
                            Resource.Error(ErrorCode.ERR_INTERNET_CONNECTION, t.message ?: "", null)
                    }
                    is SocketTimeoutException ->{
                        result.value =
                            Resource.Error(ErrorCode.REQUEST_TIME_OUT, t.message ?: "", null)
                    }
                    is HttpException ->{
                        result.value =
                            Resource.Error(t.code(), t.message ?: "", null)
                    }
                }
            }

        })
    }

    fun getListUser() : LiveData<Resource<List<ItemListStoryEntity>>> {
        return result
    }

    fun getLogout() {
        viewModelScope.launch {
            dataStore.logoutFromDataStore()
        }
    }
}