package com.riezki.storyapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResultEntity (
    val name: String? = null,
    val token: String? = null,
    val idUser: String? = null
) : Parcelable