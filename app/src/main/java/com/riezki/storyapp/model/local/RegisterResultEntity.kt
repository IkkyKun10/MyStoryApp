package com.riezki.storyapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResultEntity(
    val message: String? = null,
    val error: Boolean? = null
) : Parcelable
