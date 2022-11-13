package com.riezki.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.utils.DataDummy
import com.riezki.storyapp.utils.MainDispatcherRule
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryAppViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var viewModel: ListStoryAppViewModel

    @Mock
    private lateinit var dataStore: DataStorePreference

    private val dummyPagingStory = DataDummy.generateDummyPagedStory()

    @Before
    fun setUp() {
        viewModel = ListStoryAppViewModel(dataStore, storyRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val data: Resource<PagingData<ItemListStoryEntity>> = Resource.Success(StoryPagingSource.snapshot(dummyPagingStory))
        val expectedStory = MutableLiveData<Resource<PagingData<ItemListStoryEntity>>>()
        expectedStory.value = data
        `when`(storyRepository.getStoryUser(dummyToken)).thenReturn(expectedStory)

        val actualResult = viewModel.getListStory(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(actualResult.data!!)

        Mockito.verify(storyRepository).getStoryUser(dummyToken)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyPagingStory, differ.snapshot())
        Assert.assertEquals(dummyPagingStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyPagingStory[0].name, differ.snapshot()[0]?.name)

    }

    @Test
    fun `when Get Story Error and Return Error`() = runTest {
        val data: Resource<PagingData<ItemListStoryEntity>> = Resource.Error(401, "Bad Request", null)
        val expectedStory = MutableLiveData<Resource<PagingData<ItemListStoryEntity>>>()
        expectedStory.value = data
        `when`(storyRepository.getStoryUser(dummyToken)).thenReturn(expectedStory)

        val actualResult = viewModel.getListStory(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListCallback,
            workerDispatcher = Dispatchers.Main
        )

        Mockito.verify(storyRepository).getStoryUser(dummyToken)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(actualResult is Resource.Error)

    }

    companion object {
        private const val dummyToken = "Bearer token_user"
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

private val noopListCallback = object : ListUpdateCallback{
    override fun onInserted(position: Int, count: Int) {}

    override fun onRemoved(position: Int, count: Int) {}

    override fun onMoved(fromPosition: Int, toPosition: Int) {}

    override fun onChanged(position: Int, count: Int, payload: Any?) {}

}