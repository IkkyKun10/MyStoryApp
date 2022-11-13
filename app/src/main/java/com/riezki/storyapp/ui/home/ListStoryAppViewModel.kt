package com.riezki.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.utils.Resource
import kotlinx.coroutines.launch

class ListStoryAppViewModel(
    private val dataStore: DataStorePreference,
    private val storyRepository: StoryRepository
) : ViewModel() {

    val userTokenFromDataStore by lazy {
        dataStore.readTokenFromDataStore.asLiveData()
    }

    fun getListStory(token: String) : LiveData<Resource<PagingData<ItemListStoryEntity>>> {
        return storyRepository.getStoryUser(token)
    }


    fun getLogout() {
        viewModelScope.launch {
            dataStore.logoutFromDataStore()
        }
    }
}