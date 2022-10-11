package com.riezki.storyapp.ui.authenticasion.register

import androidx.lifecycle.ViewModel
import com.riezki.storyapp.network.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    companion object {
        private const val TAG = "register ViewModel"
    }

    fun getRegisterUser(name: String, email: String, password: String) =
        storyRepository.getRegisterUser(name, email, password)

//    private var result = MutableLiveData<Resource<RegisterResultEntity>>()
//
//    fun setRegister(name: String, email: String, password: String) {
//        val apiService = ApiConfig().getApiService()
//
//        try {
//
//            val param = mutableMapOf<String, String>()
//            param["name"] = name
//            param["email"] = email
//            param["password"] = password
//
//            apiService.registerUser(param).enqueue(object : Callback<RegisterResponse> {
//                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
//                    val responseBody = response.body()
//
//                    if (response.isSuccessful) {
//                        result.postValue(
//                            Resource.Success(
//                                RegisterResultEntity(
//                                    message = responseBody?.message ?: "",
//                                )
//                            )
//                        )
//                    } else {
//                        result.value = Resource.Error(response.code(), response.message(), null)
//                    }
//                }
//
//                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
//                    when (t) {
//                        is UnknownHostException ->{
//                            result.value =
//                                Resource.Error(ErrorCode.ERR_INTERNET_CONNECTION, t.message ?: "", null)
//                        }
//                        is SocketTimeoutException ->{
//                            result.value =
//                                Resource.Error(ErrorCode.REQUEST_TIME_OUT, t.message ?: "", null)
//                        }
//                        is HttpException ->{
//                            result.value =
//                                Resource.Error(t.code(), t.message ?: "", null)
//                        }
//                    }
//                }
//
//            })
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e(TAG, "Register User: ${e.message}")
//        }
//
//    }
//
//    fun getRegisterUser() : LiveData<Resource<RegisterResultEntity>> {
//        return result
//    }
}