package com.riezki.storyapp.di

import android.content.Context
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.network.api.ApiConfig
import com.riezki.storyapp.ui.authenticasion.login.dataStore

object Injection {
    fun provideRepository(): StoryRepository {
        val apiService = ApiConfig().getApiService()
        return StoryRepository.getInstance(apiService)
    }

    fun provideDataStore(context: Context): DataStorePreference {
        return DataStorePreference(context.dataStore)
    }
}
