package com.riezki.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.riezki.storyapp.databinding.FragmentListStoryBinding
import com.riezki.storyapp.databinding.ItemListStoryBinding
import com.riezki.storyapp.model.local.ItemListStoryEntity
import com.riezki.storyapp.paging.adapter.LoadingStateAdapter
import com.riezki.storyapp.ui.addstory.AddStoryActivity
import com.riezki.storyapp.ui.detail.DetailActivity
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.ViewModelFactory

class ListStoryFragment : Fragment(), ListStoryAdapter.StoryCallback {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListStoryAppViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: ListStoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showListAdapter()

        binding.fabAddStory.setOnClickListener {
            Intent(context, AddStoryActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        }

        getListDataServer()
    }

    private fun getListDataServer() {
        var token = ""
        viewModel.userTokenFromDataStore.observe(viewLifecycleOwner) {
            token = it

            viewModel.getListStory("Bearer $token").observe(viewLifecycleOwner) { list ->
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
                            Toast.makeText(context, "List Gagal ditampilkan", Toast.LENGTH_SHORT).show()
                            binding.errorPage.visibility = View.VISIBLE
                        }

                    }
                }
            }
        }
    }

    private fun showListAdapter() {
        adapter = ListStoryAdapter(this)
        binding.rvListStory.layoutManager = LinearLayoutManager(context)
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        binding.rvListStory.setHasFixedSize(true)
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE else View.GONE
    }

    override fun onItemClick(itemListStory: ItemListStoryEntity) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DETAIL, itemListStory)
        val binding: ItemListStoryBinding = ItemListStoryBinding.inflate(layoutInflater)
        val optionCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(binding.tvItemName, "username"),
                Pair(binding.imgItemStory, "image_story")
            )
        startActivity(intent, optionCompat.toBundle())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}