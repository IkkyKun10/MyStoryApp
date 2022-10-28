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
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("register")
    suspend fun registerUser(
        @Body param: Map<String, String>
    ): RegisterResponse

    @GET("stories")
    suspend fun getListUser(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10
    ): ListStoryResponse

    @GET("stories")
    suspend fun getMapStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int? = 10,
        @Query("location") location: Int? = 1
    ) : ListStoryResponse

    @GET("stories")
    fun getListImageUser(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10
    ): Call<ListStoryResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        //@Part("description") description: RequestBody,
        @PartMap params: HashMap<String, RequestBody>?,
    ): AddNewStoryResponse
}