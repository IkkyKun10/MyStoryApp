package com.riezki.storyapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddNewStoryResultEntity(
    val message: String? = null,
    val error: Boolean? = null
) : Parcelable
