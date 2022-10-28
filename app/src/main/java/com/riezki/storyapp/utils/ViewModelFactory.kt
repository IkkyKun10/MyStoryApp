package com.riezki.storyapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.riezki.storyapp.di.Injection
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.ui.addstory.AddStoryViewModel
import com.riezki.storyapp.ui.authenticasion.login.LoginViewModel
import com.riezki.storyapp.ui.authenticasion.register.RegisterViewModel
import com.riezki.storyapp.ui.home.ListStoryAppViewModel
import com.riezki.storyapp.ui.maps.MapsViewModel
import com.riezki.storyapp.ui.splash_screen.SplashScreenViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val dataStoreRepository: DataStorePreference,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(dataStoreRepository) as T
            }
            modelClass.isAssignableFrom(ListStoryAppViewModel::class.java) -> {
                ListStoryAppViewModel(dataStoreRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(dataStoreRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(dataStoreRepository, storyRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideDataStore(context), Injection.provideRepository(context))
            }.also { instance = it }
    }
}