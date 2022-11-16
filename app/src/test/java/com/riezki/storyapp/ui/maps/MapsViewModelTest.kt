package com.riezki.storyapp.ui.maps

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.ui.home.ListStoryAppViewModelTest
import com.riezki.storyapp.utils.DataDummy
import com.riezki.storyapp.utils.MainDispatcherRule
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var context: Context

    @Mock
    lateinit var dataStore: DataStorePreference
    private val dummyStory = DataDummy.generateDummyStory()

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        mapsViewModel = MapsViewModel(dataStore, storyRepository)
    }

    @Test
    fun `when Get List Maps Story Should Not Null and Return Success`() = runTest {

        val expectedStory = MutableLiveData<Resource<List<ItemListStoryEntity>?>>()
        expectedStory.value = Resource.Success(dummyStory)
        `when`(storyRepository.getMapStory(context, dummyToken, page = 1)).thenReturn(expectedStory)

        val actualStory = mapsViewModel.getListMap(context, dummyToken).getOrAwaitValue()

        Mockito.verify(storyRepository).getMapStory(context, dummyToken, 1)
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Resource.Success)
        Assert.assertEquals(dummyStory.size, (actualStory as Resource.Success).data?.size)
    }

    @Test
    fun `when Get List Maps Story Error and Return Error`() = runTest {

        val expectedStory = MutableLiveData<Resource<List<ItemListStoryEntity>?>>()
        expectedStory.value = Resource.Error(400, "Bad Request", null)
        `when`(storyRepository.getMapStory(context, dummyToken, page = 1)).thenReturn(expectedStory)

        val actualStory = mapsViewModel.getListMap(context, dummyToken).getOrAwaitValue()

        Mockito.verify(storyRepository).getMapStory(context, dummyToken, 1)
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Resource.Error)
    }

    @Test
    fun `when should read token`() = runTest {
        val expected: Flow<String> = flow { emit(dummyToken) }
        `when`(dataStore.readTokenFromDataStore).thenReturn(expected)

        val actual = mapsViewModel.userToken.getOrAwaitValue()

        Mockito.verify(dataStore).readTokenFromDataStore
        Assert.assertNotNull(actual)
        Assert.assertEquals(dummyToken, actual)
    }

    companion object {
        private const val dummyToken =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWRlX29wd1dmN0s4QVhNSTkiLCJpYXQiOjE2NjcwNDYxMzN9._TmFRjqq3BL8R--c1mIDTB2UwaJfFDCv_kt8lxNfnMU"
    }

}