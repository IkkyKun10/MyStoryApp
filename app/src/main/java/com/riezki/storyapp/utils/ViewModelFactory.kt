package com.riezki.storyapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.ui.addstory.AddStoryViewModel
import com.riezki.storyapp.ui.authenticasion.login.LoginViewModel
import com.riezki.storyapp.ui.home.ListStoryAppViewModel
import com.riezki.storyapp.ui.splash_screen.SplashScreenViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val dataStoreRepository: DataStorePreference) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(dataStoreRepository) as T
            }
            modelClass.isAssignableFrom(ListStoryAppViewModel::class.java) -> {
                ListStoryAppViewModel(dataStoreRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel() as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(dataStoreRepository) as T
            }
//            modelClass.isAssignableFrom(RegisterActivity::class.java) -> {
//                RegisterViewModel() as T
//            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }
}