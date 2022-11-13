package com.riezki.storyapp.network

import android.content.Context
import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.riezki.storyapp.data.FakeApiService
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.network.api.ApiService
import com.riezki.storyapp.paging.database.StoryDatabase
import com.riezki.storyapp.ui.home.ListStoryAdapter
import com.riezki.storyapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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

    private lateinit var location: Location

    private lateinit var apiService: ApiService
    private lateinit var storyRepository: StoryRepository
    private lateinit var context: Context

    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Before
    fun setUp() {
        location = mock(Location::class.java)
        context = mock(Context::class.java)
        apiService = FakeApiService()
        storyRepository = StoryRepository(apiService, mockDb)
    }

    @Test
    fun `when register success`() = runTest {
        val expectedRegisterResponse = DataDummy.generateRegisterStoryResponse()
        val params = mutableMapOf<String, String>()
        params["name"] = dummyName
        params["email"] = dummyEmail
        params["password"] = dummyPassword
        val actualRegister = storyRepository.getRegisterUser(dummyName, dummyEmail, dummyPassword)
        actualRegister.observeForTesting {
            Assert.assertNotNull(actualRegister)
            Assert.assertEquals(expectedRegisterResponse.message, (actualRegister.value as Resource.Success).data?.message)
        }
    }

    @Test
    fun `when login success`() = runTest {
        val expectedLogin = DataDummy.generateLoginStoryResponse()
        val actualLogin = storyRepository.getLoginUser(dummyEmail, dummyPassword)
        actualLogin.observeForTesting {
            Assert.assertNotNull(actualLogin)
            Assert.assertEquals(expectedLogin.loginResult?.name, (actualLogin.value as Resource.Success).data?.name)
        }
    }

    @Test
    fun `when get map success`() = runTest {
        val expectedMaps = DataDummy.generateDummyStoryResponse()
        val actualMaps = storyRepository.getMapStory(context, dummyToken, dummyPage)
        actualMaps.observeForTesting {
            Assert.assertNotNull(actualMaps)
            Assert.assertEquals(expectedMaps.size, (actualMaps.value as Resource.Success).data?.size)
        }
    }

    @Test
    fun `when get story success`() = runTest {
        val data= DataDummy.generateDummyPagedStoryRepo()
        val expect = Resource.Success(StoryPagingSource.snapshot(data))
        val expectedResult = MutableLiveData<Resource<PagingData<ItemListStoryEntity>>>()
        expectedResult.value = expect

        val actualResult = storyRepository.getStoryUser(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListCallback,
            workerDispatcher = Dispatchers.Main
        )

        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(actualResult is Resource.Success)
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

        val expectedResult = DataDummy.generateAddNewStoryResponse()

        val actualResult = storyRepository.setUploadImage(context, dummyToken, imageMultipart, description, location)
        actualResult.observeForTesting {
            Assert.assertNotNull(actualResult)
            Assert.assertEquals(expectedResult.message, (actualResult.value as Resource.Success).data?.message)
        }
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<ItemListStoryEntity>>>() {

        companion object {
            fun snapshot(items: List<ItemListStoryEntity>) : PagingData<ItemListStoryEntity> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ItemListStoryEntity>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ItemListStoryEntity>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

    private val noopListCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}

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