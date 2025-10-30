package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "video",
    indices = [Index(value = ["slug"], unique = true)]
)
data class Video(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val slug: String,
    val detailMovie: DetailMovie
)
