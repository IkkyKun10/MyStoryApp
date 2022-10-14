package com.riezki.storyapp.ui.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.utils.Resource
import kotlinx.coroutines.launch

class ListStoryAppViewModel(
    private val dataStore: DataStorePreference,
    private val storyRepository: StoryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ListStoryViewModel"
    }

    val userTokenFromDataStore = dataStore.readTokenFromDataStore.asLiveData()

    //private var result = MutableLiveData<Resource<List<ItemListStoryEntity>>>()

    fun getListStory(token: String) : LiveData<Resource<PagingData<ItemListStoryEntity>>> {
        return storyRepository.getStoryUser(token)
    }


    fun getLogout() {
        viewModelScope.launch {
            dataStore.logoutFromDataStore()
        }
    }
}