package com.riezki.storyapp.paging.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.riezki.storyapp.model.local.ItemListStoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStory(quote: List<ItemListStoryEntity>?)

    @Query("SELECT * from item_story")
    fun getAllStory() : PagingSource<Int, ItemListStoryEntity>

    @Query("DELETE from item_story")
    suspend fun deleteAll()
}