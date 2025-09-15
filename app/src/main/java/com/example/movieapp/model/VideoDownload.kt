package com.example.movieapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "video_downloads")
@Parcelize
data class VideoDownload(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val thumb: String,
    val name: String,
    val slug: String,
    val detailMovie: DetailMovie
) : Parcelable