package com.riezki.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.riezki.storyapp.R
import com.riezki.storyapp.databinding.ActivityDetailBinding
import com.riezki.storyapp.model.local.ItemListStoryEntity

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<ItemListStoryEntity>(EXTRA_DETAIL)

        binding.backToList.setOnClickListener { onBackPressed() }
        showDetailView(data)
    }

    private fun showDetailView(itemListStoryEntity: ItemListStoryEntity?) {
        binding.imageviewDetail.load(itemListStoryEntity?.photoUrl) {
            error(R.drawable.ic_error_image)
            placeholder(R.drawable.ic_loading)
        }
        binding.usernameDetail.text = itemListStoryEntity?.name
        binding.descDetail.text = itemListStoryEntity?.description

    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}