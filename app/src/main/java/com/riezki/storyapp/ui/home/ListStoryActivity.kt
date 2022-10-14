package com.riezki.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.riezki.storyapp.R
import com.riezki.storyapp.databinding.ActivityListStoryBinding
import com.riezki.storyapp.databinding.ItemListStoryBinding
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.paging.adapter.LoadingStateAdapter
import com.riezki.storyapp.ui.addstory.AddStoryActivity
import com.riezki.storyapp.ui.authenticasion.login.LoginActivity
import com.riezki.storyapp.ui.authenticasion.login.dataStore
import com.riezki.storyapp.ui.detail.DetailActivity
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

class ListStoryActivity : AppCompatActivity(), ListStoryAdapter.StoryCallback {

    private lateinit var binding: ActivityListStoryBinding
    private val viewModel: ListStoryAppViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.elevation = 0f
        showListAdapter()

        binding.fabAddStory.setOnClickListener {
            Intent(this, AddStoryActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        getListDataServer()
    }

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