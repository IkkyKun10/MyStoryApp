package com.riezki.storyapp.di

import android.content.Context
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.network.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig().getApiService()
        return StoryRepository.getInstance(apiService)
    }
}
