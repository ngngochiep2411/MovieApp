package com.example.movieapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.model.VideoDownload

@Database(entities = [MovieHistory::class, VideoDownload::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
}