package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val slug: String,
    val detailMovie: DetailMovie
)
