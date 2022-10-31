package com.riezki.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.utils.DataDummy
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var dataStore: DataStorePreference
    private val dummyStory = DataDummy.generateDummyStory()

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(dataStore, storyRepository)
    }


}