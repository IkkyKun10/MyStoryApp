package com.riezki.storyapp.model.local

import com.google.gson.annotations.SerializedName

data class ListStoryEntity(
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val name: String? = null,
    val description: String? = null,
    val idUser: String? = null,
)
