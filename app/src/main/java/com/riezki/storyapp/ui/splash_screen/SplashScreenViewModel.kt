package com.riezki.storyapp.ui.splash_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.riezki.storyapp.model.preference.DataStorePreference

class SplashScreenViewModel(dataStorePreference: DataStorePreference) : ViewModel() {
    val readStateLogin: LiveData<Boolean> by lazy {
        dataStorePreference.readLoginStateFromDataStore.asLiveData()
    }
}