package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class MovieHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int
)