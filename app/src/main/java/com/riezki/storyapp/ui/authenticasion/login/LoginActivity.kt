package com.riezki.storyapp.ui.authenticasion.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.riezki.storyapp.databinding.ActivityLoginBinding
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.model.preference.PREFERENCE_NAME
import com.riezki.storyapp.ui.authenticasion.register.RegisterActivity
import com.riezki.storyapp.ui.home.ListStoryActivity
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        supportActionBar?.elevation = 0f

        binding.btnLogin.setOnClickListener {
            if (validateLogin()) {
                getLogin()
            }

        }

        binding.register.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

        playAnimation()
    }

    private fun getLogin() {
        //showLoading(true)
        viewModel.getLoginUser(
            binding.tilEmail.text.toString(),
            binding.tilPassword.text.toString()
        ).observe(this@LoginActivity) {
            //showLoading(false)
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    viewModel.saveTokenDataStore(DataStorePreference(dataStore), it.data?.token ?: "")
                    viewModel.saveLoginStateDataStore(DataStorePreference(dataStore))
                    Intent(this, ListStoryActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Login gagal, Silahkan coba lagi", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun validateLogin(): Boolean {
        with(binding) {
            return when {
                tilEmail.isError || tilEmail.text?.isEmpty() == true -> {
                    tilEmail.requestFocus()
                    false
                }

                tilPassword.isError || tilPassword.text?.isEmpty() == true -> {
                    tilPassword.requestFocus()
                    false

                }
                else -> {
                    tilEmail.error = null
                    tilPassword.error = null
                    true
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.tvNameApp, View.TRANSLATION_X, -15f, 15f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvNameActivity = ObjectAnimator.ofFloat(binding.tvNameApp, View.ALPHA, 1f).setDuration(100)
        val imgAccount = ObjectAnimator.ofFloat(binding.imgAccount, View.ALPHA, 1f).setDuration(100)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val fieldEmail = ObjectAnimator.ofFloat(binding.fieldLayoutEmail, View.ALPHA, 1f).setDuration(100)
        val edtEmail = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(100)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val fieldPwd = ObjectAnimator.ofFloat(binding.fieldLayoutPwd, View.ALPHA, 1f).setDuration(100)
        val edtPassword = ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(100)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val tvRegister = ObjectAnimator.ofFloat(binding.register, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(imgAccount, tvNameActivity)
        }

        AnimatorSet().apply {
            playSequentially(
                together, tvEmail, fieldEmail, edtEmail, tvPassword, fieldPwd, edtPassword, btnLogin, tvRegister
            )
            start()
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE else View.GONE
    }
}