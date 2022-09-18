package com.riezki.storyapp.ui.authenticasion.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.riezki.storyapp.databinding.ActivityRegisterBinding
import com.riezki.storyapp.ui.authenticasion.login.LoginActivity
import com.riezki.storyapp.utils.Resource

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        supportActionBar?.elevation = 0f

        binding.btnRegister.setOnClickListener {
            if (validateLogin()) {
                getRegister()
            }
        }

        binding.fabBack.setOnClickListener { onBackPressed() }

        playAnimation()
    }

    private fun getRegister() {
        showLoading(true)
        viewModel.setRegister(
            binding.tilUsername.text.toString(),
            binding.tilEmail.text.toString(),
            binding.tilPassword.text.toString()
        )
        viewModel.getRegisterUser().observe(this) {
            showLoading(false)
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
//                    if (it.data?.error == true) {
//                        Toast.makeText(
//                            this, "Register Failed", Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    else {
                        Toast.makeText(this, "${it.data?.message}", Toast.LENGTH_SHORT
                        ).show()
                        Intent(this, LoginActivity::class.java).also { intent ->
                            startActivity(intent)
                            finish()
                        }
                }

                is Resource.Error -> {
                    Toast.makeText(
                        this, "Maaf pendaftaran account gagal, silahkan ulangi kembali", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateLogin() : Boolean {
        with(binding) {
            return when{
                tilEmail.isError || tilEmail.text?.isEmpty() == true -> {
                    //tilEmail.requestFocus()
                    tilEmail.error = "Silahkan isi email anda terlebih dahulu"
                    false
                }

                tilPassword.isError || tilPassword.text?.isEmpty() == true -> {
                    //tilPassword.requestFocus()
                    tilPassword.error = "Silahkan isi password anda terlebih dahulu"
                    false

                } else -> {
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

        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val tvUsername = ObjectAnimator.ofFloat(binding.tvUsername, View.ALPHA, 1f).setDuration(100)
        val tilUsername = ObjectAnimator.ofFloat(binding.tilUsername, View.ALPHA, 1f).setDuration(100)
        val fabBack = ObjectAnimator.ofFloat(binding.fabBack, View.ALPHA, 1f).setDuration(100)
        val imgAccount = ObjectAnimator.ofFloat(binding.imgAccount, View.ALPHA, 1f).setDuration(100)
        val tilEmail = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(100)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val tilPassword = ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(100)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(fabBack, imgAccount)
        }

        AnimatorSet().apply {
            playSequentially(together, tvUsername, tilUsername, tvEmail, tilEmail, tvPassword, tilPassword, btnLogin)
            start()
        }
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}