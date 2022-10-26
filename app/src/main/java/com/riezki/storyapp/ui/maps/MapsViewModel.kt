package com.riezki.storyapp.ui.maps

import android.content.Context
import androidx.lifecycle.ViewModel
import com.riezki.storyapp.network.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getListMap(context: Context, token: String, page: Int = 1, size: Int, location: Int) =
        storyRepository.getMapStory(context, token, page, size, location)

}