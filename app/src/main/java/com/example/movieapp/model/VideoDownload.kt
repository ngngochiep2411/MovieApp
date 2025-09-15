package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_downloads")
data class VideoDownload(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val thumb: String,
    val name: String,
    val slug: String
)