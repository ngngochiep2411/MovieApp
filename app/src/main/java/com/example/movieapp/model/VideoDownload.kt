package com.example.movieapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "video_downloads",
    indices = [Index(value = ["slug"], unique = true)]
)
@Parcelize
data class VideoDownload(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val thumb: String,
    val name: String,
    val slug: String,
    val detailMovie: DetailMovie
) : Parcelable