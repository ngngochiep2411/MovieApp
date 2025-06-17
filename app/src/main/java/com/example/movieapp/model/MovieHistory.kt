package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie_view_history")
data class MovieHistory(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
    @PrimaryKey
    @field:SerializedName("slug")
    val slug: String = "",
    @field:SerializedName("thumb_url")
    val thumbUrl: String? = "",
    @field:SerializedName("name")
    val name: String? = "",
)