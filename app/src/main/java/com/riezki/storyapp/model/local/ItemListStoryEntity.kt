package com.riezki.storyapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemListStoryEntity(
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val name: String? = null,
    val description: String? = null,
    val idUser: String? = null,
) : Parcelable
