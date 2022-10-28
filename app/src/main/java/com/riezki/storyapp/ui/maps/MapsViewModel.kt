package com.riezki.storyapp.ui.maps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository

class MapsViewModel(
    dataStore: DataStorePreference,
    private val storyRepository: StoryRepository
    ) : ViewModel() {

    val userToken = dataStore.readTokenFromDataStore.asLiveData()

    fun getListMap(context: Context, token: String, page: Int = 1) =
        storyRepository.getMapStory(context, token, page)

}