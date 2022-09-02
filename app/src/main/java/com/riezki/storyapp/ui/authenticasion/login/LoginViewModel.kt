package com.riezki.storyapp.ui.authenticasion.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.riezki.storyapp.model.local.LoginResultEntity
import com.riezki.storyapp.model.response.LoginResponse
import com.riezki.storyapp.network.api.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val TAG = "login ViewModel"

    private var result = MutableLiveData<LoginResultEntity>()

    fun setLogin(email: String, password: String) {
        val apiService = ApiConfig().getApiService()

        apiService.loginUser(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getLoginUser() : LiveData<LoginResultEntity> {
        return result
    }
}