package com.riezki.storyapp.ui.authenticasion.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riezki.storyapp.model.local.LoginResultEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.model.response.LoginResponse
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.network.api.ApiConfig
import com.riezki.storyapp.utils.ErrorCode
import com.riezki.storyapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getLoginUser(email: String, password: String) = storyRepository.getLoginUser(email, password)

    fun saveTokenDataStore(dataStoreSource: DataStorePreference, token: String) {
        viewModelScope.launch {
            dataStoreSource.saveTokenToDataStore(token)
        }
    }

    fun saveLoginStateDataStore(dataStoreSource: DataStorePreference) {
        viewModelScope.launch {
            dataStoreSource.saveLoginToDataStore()
        }
    }


//    private var result = MutableLiveData<Resource<LoginResultEntity>>()
//
//    fun setLogin(email: String, password: String) {
//        val apiService = ApiConfig().getApiService()
//
//        apiService.loginUser(email, password).enqueue(object : Callback<LoginResponse> {
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                val responseBody = response.body()
//
//                if (response.isSuccessful) {
//                    result.postValue(
//                        Resource.Success(
//                            LoginResultEntity(
//                                name = responseBody?.loginResult?.name ?: "",
//                                token = responseBody?.loginResult?.token ?: "",
//                                idUser = responseBody?.loginResult?.userId ?: ""
//                            )
//                        )
//                    )
//                } else {
//                    result.value = Resource.Error(response.code(), response.message(), null)
//                }
//            }
//
//            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                when (t) {
//                    is UnknownHostException -> {
//                        result.value =
//                            Resource.Error(ErrorCode.ERR_INTERNET_CONNECTION, t.message ?: "", null)
//                    }
//                    is SocketTimeoutException -> {
//                        result.value =
//                            Resource.Error(ErrorCode.REQUEST_TIME_OUT, t.message ?: "", null)
//                    }
//                    is HttpException -> {
//                        result.value =
//                            Resource.Error(t.code(), t.message ?: "", null)
//                    }
//                }
//            }
//
//        })
//    }
//
//    fun getLoginUser(): LiveData<Resource<LoginResultEntity>> {
//        return result
//    }
}