package com.riezki.storyapp.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.riezki.storyapp.data.FakeApiService
import com.riezki.storyapp.network.api.ApiService
import com.riezki.storyapp.paging.database.StoryDatabase
import com.riezki.storyapp.utils.DataDummy
import com.riezki.storyapp.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import java.io.File


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class StoryRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var apiService: ApiService
    private lateinit var storyRepository: StoryRepository

    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Before
    fun setUp() {
        apiService = FakeApiService()
        storyRepository = StoryRepository(apiService, mockDb)
    }

    @Test
    fun `when register success`() = runTest {
        val expectedRegisterResponse = DataDummy.generateRegisterStory()
        val params = mutableMapOf<String, String>()
        params["name"] = dummyName
        params["email"] = dummyEmail
        params["password"] = dummyPassword
        val actualRegister = apiService.registerUser(params)
        Assert.assertNotNull(actualRegister)
        Assert.assertEquals(expectedRegisterResponse.message, actualRegister.message)
    }

    @Test
    fun `when login success`() = runTest {
        val expectedLogin = DataDummy.generateLoginStory()
        val actualLogin = apiService.loginUser(dummyEmail, dummyPassword)
        Assert.assertNotNull(actualLogin)
        Assert.assertEquals(expectedLogin.name, actualLogin.loginResult?.name)
    }

    @Test
    fun `when get map success`() = runTest {
        val expectedMaps = DataDummy.generateDummyStory()
        val actualMaps = apiService.getMapStory(dummyToken, dummyPage, dummySize, 1)
        Assert.assertNotNull(actualMaps)
        Assert.assertEquals(expectedMaps.size, actualMaps.listStory?.size)
    }

    @Test
    fun `when get story success`() = runTest {
        val expectedResult = DataDummy.generateDummyPagedStoryRepo()
        val actualResult = apiService.getListUser(dummyToken)
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(expectedResult.size, actualResult.listStory?.size)
    }

    @Test
    fun `when upload image success`() = runTest {
        val description =
            "Ini adalah deskripsi sebuah gambar".toRequestBody("text/plain".toMediaType())
        val file = mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "nameFile",
            requestImageFile
        )

        val expectedResult = DataDummy.generateAddNewStory()
        val actualResult = apiService.uploadImage(
            dummyToken,
            imageMultipart,
            HashMap(
                mutableMapOf<String, RequestBody>().apply {
                    put("description", description)
                    put("lat", dummyLatitude)
                    put("lon", dummyLongitude)
                }
            )
        )
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(expectedResult.message, actualResult.message)
    }

    companion object {
        private const val dummyToken = "user_token"
        private const val dummyName = "kiki"
        private const val dummyEmail = "kiki@gmail.com"
        private const val dummyPassword = "123456"
        private const val dummyPage = 1
        private const val dummySize = 10
        private val dummyLatitude = (-10.212).toString().toRequestBody("text/plain".toMediaType())
        private val dummyLongitude = (-16.002).toString().toRequestBody("text/plain".toMediaType())
    }
}