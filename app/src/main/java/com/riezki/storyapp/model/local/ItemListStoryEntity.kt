package com.riezki.storyapp.model.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "item_story")
data class ItemListStoryEntity(

    @field:SerializedName("photo_url")
    val photoUrl: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @PrimaryKey
    @field:SerializedName("id_user")
    val idUser: String,

    @field:SerializedName("latitude")
    val lat: Double? = null,

    @field:SerializedName("longitude")
    val lon: Double? = null
) : Parcelable
