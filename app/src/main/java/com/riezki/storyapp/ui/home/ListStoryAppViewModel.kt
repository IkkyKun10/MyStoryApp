package com.riezki.storyapp.ui.home

import androidx.lifecycle.*
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
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

    fun getListStory(token: String) = storyRepository.getStoryUser(token)

    fun getLogout() {
        viewModelScope.launch {
            dataStore.logoutFromDataStore()
        }
    }
}