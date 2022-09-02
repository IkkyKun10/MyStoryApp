package com.riezki.storyapp.network.api

import com.riezki.storyapp.model.response.AddNewStoryResponse
import com.riezki.storyapp.model.response.ListStoryResponse
import com.riezki.storyapp.model.response.LoginResponse
import com.riezki.storyapp.model.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @FormUrlEncoded
    @POST("/register")
    fun registerUser(
        @Body param: Map<String, String>
    ) : Call<RegisterResponse>

    @GET("/stories")
    fun getListUser(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : Call<ListStoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddNewStoryResponse>
}