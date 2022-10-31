package com.riezki.storyapp.utils

import com.riezki.storyapp.model.local.ItemListStoryEntity

object DataDummy {
    fun generateDummyStory() : List<ItemListStoryEntity> {
        val listStory = ArrayList<ItemListStoryEntity>()

        for (i in 0..10) {
            val story = ItemListStoryEntity(
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                name = "Dimas",
                lat = -10.212,
                lon =  -16.002,
                description = "Lorem Ipsum",
                idUser = "story-FvU4u0Vp2S3PMsFg"
            )

            listStory.add(story)
        }
        return listStory
    }
}