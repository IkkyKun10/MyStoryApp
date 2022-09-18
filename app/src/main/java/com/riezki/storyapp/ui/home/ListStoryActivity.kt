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
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.riezki.storyapp.R
import com.riezki.storyapp.databinding.ActivityListStoryBinding
import com.riezki.storyapp.databinding.ItemListStoryBinding
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.ui.addstory.AddStoryActivity
import com.riezki.storyapp.ui.authenticasion.login.LoginActivity
import com.riezki.storyapp.ui.authenticasion.login.dataStore
import com.riezki.storyapp.ui.detail.DetailActivity
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.ViewModelFactory

class ListStoryActivity : AppCompatActivity(), ListStoryAdapter.StoryCallback {

    private lateinit var binding: ActivityListStoryBinding
    private val viewModel: ListStoryAppViewModel by viewModels {
        ViewModelFactory(DataStorePreference(dataStore))
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
            }
        }

        getListDataServer()
    }

    private fun getListDataServer() {
        showLoading(true)
        var token = ""
        viewModel.userTokenFromDataStore.observe(this) {
            token = it

            viewModel.setListStory("Bearer $token")
            viewModel.getListUser().observe(this) { list ->
                showLoading(false)
                if (list != null) {
                    when (list) {
                        is Resource.Loading -> {

                        }

                        is Resource.Success -> {

                            adapter.submitList(list.data)
                        }

                        is Resource.Error -> {

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
        binding.rvListStory.adapter = adapter
        binding.rvListStory.setHasFixedSize(true)
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
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