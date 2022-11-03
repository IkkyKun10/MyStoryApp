package com.riezki.storyapp.ui.addstory

import android.content.Context
import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.riezki.storyapp.model.local.AddNewStoryResultEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.ui.authenticasion.login.dataStore
import com.riezki.storyapp.utils.DataDummy
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var viewModel: AddStoryViewModel
    private val dummyAddStory = DataDummy.generateAddNewStory()

    @Mock
    private lateinit var dataStore: DataStorePreference
    lateinit var context: Context
    private lateinit var location: Location

    @Before
    fun setUp() {
        location = mock(Location::class.java)
        context = mock(Context::class.java)
        //dataStore = mock(DataStorePreference(context.dataStore)::class.java)
        viewModel = AddStoryViewModel(dataStore, storyRepository)
    }

    @Test
    fun `when upload should not null and return success`() {
        val description =
            "Ini adalah deskripsi sebuah gambar".toRequestBody("text/plain".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "nameFile",
            requestImageFile
        )

        val expectedResult = MutableLiveData<Resource<AddNewStoryResultEntity>>()
        expectedResult.value = Resource.Success(dummyAddStory)

        `when`(storyRepository.setUploadImage(context, dummyToken, imageMultipart, description, location)).thenReturn(expectedResult)
        val actualResult = viewModel.setUploadImage(context, dummyToken, imageMultipart, description, location).getOrAwaitValue()
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Resource.Success)
    }

    @Test
    fun `when upload story and return Error`() {
        val description =
            "Ini adalah deskripsi sebuah gambar".toRequestBody("xxx/xxx".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("xxx/xxx".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "xxx",
            "xxx",
            requestImageFile
        )

        val expectedResult = MutableLiveData<Resource<AddNewStoryResultEntity>>()
        expectedResult.value = Resource.Error(400, "Bad Request", null)

        `when`(storyRepository.setUploadImage(context, dummyToken, imageMultipart, description, location)).thenReturn(expectedResult)
        val actualResult = viewModel.setUploadImage(context, dummyToken, imageMultipart, description, location).getOrAwaitValue()
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Resource.Error)
    }

    companion object {
        private const val dummyToken =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTRka2JaQUN0S3VIekZmS28iLCJpYXQiOjE2NjMwNDg2MzZ9.3KYxKvE9K9Ko8RyGHlp66SqktCyhygSyMkPAOejMg6M"
    }
}