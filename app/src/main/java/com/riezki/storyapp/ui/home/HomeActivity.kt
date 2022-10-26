package com.riezki.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.riezki.storyapp.R
import com.riezki.storyapp.databinding.ActivityHomeBinding
import com.riezki.storyapp.ui.authenticasion.login.LoginActivity
import com.riezki.storyapp.ui.maps.MapsStoryFragment
import com.riezki.storyapp.utils.ViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val viewModel: ListStoryAppViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    //private lateinit var adapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.elevation = 0f

        /*
        showListAdapter()

            binding.fabAddStory.setOnClickListener {
                Intent(this, AddStoryActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }

            getListDataServer()
        */
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
/*

    private fun getListDataServer() {
        //showLoading(true)
        var token = ""
        viewModel.userTokenFromDataStore.observe(this) {
            token = it

            viewModel.getListStory("Bearer $token").observe(this@ListStoryActivity) { list ->
                //showLoading(false)
                if (list != null) {
                    when (list) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }

                        is Resource.Success -> {
                            showLoading(false)
                            list.data?.let { data ->
                                adapter.submitData(lifecycle, data)
                            }
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            Toast.makeText(this, "List Gagal ditampilkan", Toast.LENGTH_SHORT).show()
                            binding.errorPage.visibility = View.VISIBLE
                        }

                    }
                }
            }
        }
    }

    private fun showListAdapter() {
        adapter = ListStoryAdapter(this)
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        binding.rvListStory.setHasFixedSize(true)
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE else View.GONE
    }

    override fun onItemClick(itemListStory: ItemListStoryEntity) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DETAIL, itemListStory)
        val binding: ItemListStoryBinding = ItemListStoryBinding.inflate(layoutInflater)
        val optionCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(binding.tvItemName, "username"),
                Pair(binding.imgItemStory, "image_story")
            )
        startActivity(intent, optionCompat.toBundle())
    }
*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
                viewModel.getLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}