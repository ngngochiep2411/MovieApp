package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("playlist")
data class PlayList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playListName:String,
)