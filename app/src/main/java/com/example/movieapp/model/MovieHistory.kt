package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie_view_history")
data class MovieHistory(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
    @PrimaryKey
    val slug: String = "",
    val thumbUrl: String? = "",
    val name: String? = "",
    val episode: Int = 0,
    val watchedAt: Long = 0,
    val duration: Long = 0,
    val total: String = ""
)