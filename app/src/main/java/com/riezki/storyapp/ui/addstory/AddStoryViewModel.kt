package com.riezki.storyapp.ui.addstory

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.riezki.storyapp.model.local.NewStoryResult
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.model.response.AddNewStoryResponse
import com.riezki.storyapp.network.api.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class AddStoryViewModel(dataStore: DataStorePreference) : ViewModel() {

    val userTokenFromDataStore = dataStore.readTokenFromDataStore.asLiveData()

    private var result = MutableLiveData<NewStoryResult>()

    fun setUploadImage(context: Context, token: String, body: MultipartBody.Part, description: RequestBody)
            : LiveData<NewStoryResult> {

        val client = ApiConfig().getApiService()

        client.uploadImage(token, body, description).enqueue(object : retrofit2.Callback<AddNewStoryResponse> {
            override fun onResponse(call: Call<AddNewStoryResponse>, response: Response<AddNewStoryResponse>) {
                val responseBody = response.body()

                if (response.isSuccessful) {
                    if (responseBody != null && !responseBody.error!!) {
                        result.value = NewStoryResult(message = responseBody.message)
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                Log.e("Add New Story View Model", t.message.toString())
                Toast.makeText(context, "Gagal instance Retrofit", Toast.LENGTH_SHORT).show()
            }

        })

        return  result
    }

}