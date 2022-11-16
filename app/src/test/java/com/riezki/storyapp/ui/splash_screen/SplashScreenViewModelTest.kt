package com.riezki.storyapp.ui.splash_screen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.utils.MainDispatcherRule
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
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SplashScreenViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var preference: DataStorePreference

    private lateinit var viewModel: SplashScreenViewModel

    @Before
    fun setUp() {
        viewModel = SplashScreenViewModel(preference)
    }

    @Test
    fun `when state true return true`() = runTest {
        val expected: Flow<Boolean> = flow { emit(true) }
        Mockito.`when`(preference.readLoginStateFromDataStore).thenReturn(expected)

        val actual = viewModel.readStateLogin.getOrAwaitValue()
        Mockito.verify(preference).readLoginStateFromDataStore
        Assert.assertTrue(actual)
        Assert.assertFalse(!actual)
        Assert.assertEquals(true, actual)
    }

    @Test
    fun `when state false return false`() = runTest {
        val expected: Flow<Boolean> = flow { emit(false) }
        Mockito.`when`(preference.readLoginStateFromDataStore).thenReturn(expected)

        val actual = viewModel.readStateLogin.getOrAwaitValue()
        Mockito.verify(preference).readLoginStateFromDataStore
        Assert.assertTrue(!actual)
        Assert.assertFalse(actual)
        Assert.assertEquals(false, actual)
    }
}
