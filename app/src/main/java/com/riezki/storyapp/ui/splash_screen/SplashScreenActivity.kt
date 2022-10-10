package com.riezki.storyapp.ui.splash_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.riezki.storyapp.R
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.ui.authenticasion.login.LoginActivity
import com.riezki.storyapp.ui.authenticasion.login.dataStore
import com.riezki.storyapp.ui.home.ListStoryActivity
import com.riezki.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {
            delay(2000)

            withContext(Dispatchers.Main) {
                viewModel.readStateLogin.observe(this@SplashScreenActivity) { loginState ->
                    if (loginState) {
                        startActivity(
                            Intent(this@SplashScreenActivity, ListStoryActivity::class.java)
                        )
                        finish()
                    } else {
                        startActivity(
                            Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        )
                        finish()
                    }
                }
            }
        }
    }
}