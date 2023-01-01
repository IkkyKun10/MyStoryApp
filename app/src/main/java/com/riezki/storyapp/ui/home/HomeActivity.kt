package com.riezki.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.riezki.storyapp.R
import com.riezki.storyapp.databinding.ActivityHomeBinding
import com.riezki.storyapp.model.local.LoginResultEntity
import com.riezki.storyapp.ui.authenticasion.login.LoginActivity
import com.riezki.storyapp.ui.maps.MapsStoryFragment
import com.riezki.storyapp.utils.ViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val viewModel: ListStoryAppViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.elevation = 0f

        val dataIntent = intent.getParcelableExtra<LoginResultEntity>(EXTRA_NAME)
        supportActionBar?.title = "Halo, ${dataIntent?.name}"

        replaceFragment(ListStoryFragment())
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(ListStoryFragment())
                R.id.maps -> replaceFragment(MapsStoryFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        fragmentTransaction.replace(binding.frameLayout.id, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.logout_message))
                    .setPositiveButton("Ya") { _, _ ->
                        Intent(this, LoginActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                        viewModel.getLogout()
                    }
                    .setNegativeButton("Tidak") { _, _ ->}
                    .create()
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
    }
}