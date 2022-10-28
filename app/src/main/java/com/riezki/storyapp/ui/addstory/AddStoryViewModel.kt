package com.riezki.storyapp.ui.addstory

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.riezki.storyapp.model.local.AddNewStoryResultEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.model.response.AddNewStoryResponse
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.network.api.ApiConfig
import com.riezki.storyapp.utils.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class AddStoryViewModel(
    private val dataStore: DataStorePreference,
    private val storyRepository: StoryRepository
) : ViewModel() {

    val userTokenFromDataStore = dataStore.readTokenFromDataStore.asLiveData()

    //private var result = MutableLiveData<AddNewStoryResultEntity>()

    fun setUploadImage(
        context: Context,
        token: String,
        body: MultipartBody.Part,
        description: RequestBody,
        location: Location?
    ): LiveData<Resource<AddNewStoryResultEntity>> {
        return storyRepository.setUploadImage(context, token, body, description, location)
    }

    /*
        val client = ApiConfig().getApiService()

        client.uploadImage(token, body, description).enqueue(object : retrofit2.Callback<AddNewStoryResponse> {
            override fun onResponse(call: Call<AddNewStoryResponse>, response: Response<AddNewStoryResponse>) {
                val responseBody = response.body()

                if (response.isSuccessful) {
                    if (responseBody != null && !responseBody.error!!) {
                        result.value = AddNewStoryResultEntity(message = responseBody.message)
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
    */

}