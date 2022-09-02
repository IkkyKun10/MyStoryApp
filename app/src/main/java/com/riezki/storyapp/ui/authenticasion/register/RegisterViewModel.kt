package com.riezki.storyapp.ui.authenticasion.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.riezki.storyapp.model.local.RegisterResultEntity
import com.riezki.storyapp.model.response.RegisterResponse
import com.riezki.storyapp.network.api.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val TAG = "register ViewModel"

    private var result = MutableLiveData<RegisterResultEntity>()

    fun setRegister(name: String, email: String, password: String) {
        val apiService = ApiConfig().getApiService()

        try {

            val param = mutableMapOf<String, String>()
            param["name"] = name
            param["email"] = email
            param["password"] = password

            apiService.registerUser(param).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Register User: ${e.message}")
        }

    }

    fun getRegisterUser() : LiveData<RegisterResultEntity> {
        return result
    }
}