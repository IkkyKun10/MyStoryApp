package com.riezki.storyapp.utils

import com.riezki.storyapp.model.response.AddNewStoryResponse
import com.riezki.storyapp.model.response.ListStoryResponse
import com.riezki.storyapp.model.response.LoginResponse
import com.riezki.storyapp.model.response.RegisterResponse
import com.riezki.storyapp.network.api.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class FakeApiService : ApiService {
    private val dummyListStoryPaged = DataDummy.generateDummyPagedStoryResponse(1, 10)

    private val dummyLoginResponse = DataDummy.generateLoginStoryResponse()
    private val dummyRegister = DataDummy.generateRegisterStoryResponse()
    private val dummyMapResponse = DataDummy.generateDummyStoryResponse()
    private val dummyAddNewStoryResponse = DataDummy.generateAddNewStoryResponse()

    override suspend fun loginUser(email: String, password: String): LoginResponse {
        return dummyLoginResponse
    }

    override suspend fun registerUser(param: Map<String, String>): RegisterResponse {
        return dummyRegister
    }

    override suspend fun getListUser(token: String, page: Int?, size: Int?): ListStoryResponse {
        return ListStoryResponse(dummyListStoryPaged, false, "success")
    }

    override suspend fun getMapStory(token: String, page: Int?, size: Int?, location: Int?): ListStoryResponse {
        return ListStoryResponse(dummyMapResponse, false, "success")
    }

    override fun getListImageUser(token: String, page: Int?, size: Int?): Call<ListStoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        params: HashMap<String, RequestBody>?
    ): AddNewStoryResponse {
        return dummyAddNewStoryResponse
    }

}