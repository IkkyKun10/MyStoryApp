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