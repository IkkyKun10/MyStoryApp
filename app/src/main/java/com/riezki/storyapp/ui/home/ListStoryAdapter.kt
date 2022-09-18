package com.riezki.storyapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.riezki.storyapp.databinding.ItemListStoryBinding
import com.riezki.storyapp.model.local.ItemListStoryEntity

class ListStoryAdapter(private val callback: StoryCallback) :
    ListAdapter<ItemListStoryEntity, ListStoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemListStoryEntity>() {
            override fun areItemsTheSame(oldItem: ItemListStoryEntity, newItem: ItemListStoryEntity): Boolean =
                oldItem.idUser == newItem.idUser


            override fun areContentsTheSame(oldItem: ItemListStoryEntity, newItem: ItemListStoryEntity): Boolean =
                oldItem == newItem

        }
    }

    inner class ViewHolder(private val binding: ItemListStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemListStory: ItemListStoryEntity) {
            with(binding) {
                tvItemName.text = itemListStory.name
                imgItemStory.load(itemListStory.photoUrl)

                itemView.setOnClickListener {
                    callback.onItemClick(itemListStory)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemListViewHolder = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemListViewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    interface StoryCallback {
        fun onItemClick(itemListStory: ItemListStoryEntity)
    }

}