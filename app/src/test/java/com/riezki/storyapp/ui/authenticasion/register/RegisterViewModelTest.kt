package com.riezki.storyapp.ui.authenticasion.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.riezki.storyapp.model.local.RegisterResultEntity
import com.riezki.storyapp.network.StoryRepository
import com.riezki.storyapp.utils.DataDummy
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var registerViewModel: RegisterViewModel
    private val dummyStory = DataDummy.generateRegisterStory()

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(storyRepository)
    }

    @Test
    fun `when get register should not null and return Success`() {
        val expectedResult = MutableLiveData<Resource<RegisterResultEntity>>()
        expectedResult.value = Resource.Success(dummyStory)
        `when`(storyRepository.getRegisterUser(nameDummy, emailDummy, passDummy)).thenReturn(expectedResult)

        val actualResult = registerViewModel.getRegisterUser(nameDummy, emailDummy, passDummy).getOrAwaitValue()
        verify(storyRepository).getRegisterUser(nameDummy, emailDummy, passDummy)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Resource.Success)
    }

    @Test
    fun `when get register error and return Error`() {
        val expectedResult = MutableLiveData<Resource<RegisterResultEntity>>()
        expectedResult.value = Resource.Error(401, "Unauthorized", null)
        `when`(storyRepository.getRegisterUser(nameDummy, emailDummy, passDummy)).thenReturn(expectedResult)

        val actualResult = registerViewModel.getRegisterUser(nameDummy, emailDummy, passDummy).getOrAwaitValue()
        verify(storyRepository).getRegisterUser(nameDummy, emailDummy, passDummy)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Resource.Error)
    }

    companion object {
        private const val nameDummy = "kiki"
        private const val emailDummy = "kiki@gmail.com"
        private const val passDummy = "123456"
    }
}