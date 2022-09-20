package com.riezki.storyapp.ui.authenticasion.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.riezki.storyapp.model.local.RegisterResultEntity
import com.riezki.storyapp.model.response.RegisterResponse
import com.riezki.storyapp.network.api.ApiConfig
import com.riezki.storyapp.utils.ErrorCode
import com.riezki.storyapp.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RegisterViewModel : ViewModel() {

    companion object {
        private const val TAG = "register ViewModel"
    }

    private var result = MutableLiveData<Resource<RegisterResultEntity>>()

    fun setRegister(name: String, email: String, password: String) {
        val apiService = ApiConfig().getApiService()

        try {

            val param = mutableMapOf<String, String>()
            param["name"] = name
            param["email"] = email
            param["password"] = password

            apiService.registerUser(param).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    val responseBody = response.body()

                    if (response.isSuccessful) {
                        result.postValue(
                            Resource.Success(
                                RegisterResultEntity(
                                    message = responseBody?.message ?: "",
                                )
                            )
                        )
                    } else {
                        result.value = Resource.Error(response.code(), response.message(), null)
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    when (t) {
                        is UnknownHostException ->{
                            result.value =
                                Resource.Error(ErrorCode.ERR_INTERNET_CONNECTION, t.message ?: "", null)
                        }
                        is SocketTimeoutException ->{
                            result.value =
                                Resource.Error(ErrorCode.REQUEST_TIME_OUT, t.message ?: "", null)
                        }
                        is HttpException ->{
                            result.value =
                                Resource.Error(t.code(), t.message ?: "", null)
                        }
                    }
                }

            })

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Register User: ${e.message}")
        }

    }

    fun getRegisterUser() : LiveData<Resource<RegisterResultEntity>> {
        return result
    }
}