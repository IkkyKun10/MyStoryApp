package com.riezki.storyapp.ui.authenticasion.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.riezki.storyapp.model.local.LoginResultEntity
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyStory = DataDummy.generateLoginStory()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(storyRepository)
    }

    @Test
    fun `when get login should not null and return Success`()  {
        val expectedResult = MutableLiveData<Resource<LoginResultEntity>>()
        expectedResult.value = Resource.Success(dummyStory)
        `when`(storyRepository.getLoginUser(emailDummy, passDummy)).thenReturn(expectedResult)

        val actualResult = loginViewModel.getLoginUser(emailDummy, passDummy).getOrAwaitValue()
        verify(storyRepository).getLoginUser(emailDummy, passDummy)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Resource.Success)
    }

    @Test
    fun `when get login error and return Error`()  {
        val expectedResult = MutableLiveData<Resource<LoginResultEntity>>()
        expectedResult.value = Resource.Error(401, "Unauthorized", null)
        `when`(storyRepository.getLoginUser(emailDummy, passDummy)).thenReturn(expectedResult)

        val actualResult = loginViewModel.getLoginUser(emailDummy, passDummy).getOrAwaitValue()
        verify(storyRepository).getLoginUser(emailDummy, passDummy)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Resource.Error)
    }

    companion object {
        private const val emailDummy = "kiki@gmail.com"
        private const val passDummy = "123456"
    }
}

