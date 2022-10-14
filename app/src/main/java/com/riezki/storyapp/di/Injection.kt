package com.riezki.storyapp.di

import android.content.Context
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.network.api.ApiConfig
import com.riezki.storyapp.paging.database.StoryDatabase
import com.riezki.storyapp.ui.authenticasion.login.dataStore

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig().getApiService()
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, database)
    }

    fun provideDataStore(context: Context): DataStorePreference {
        return DataStorePreference(context.dataStore)
    }
}
