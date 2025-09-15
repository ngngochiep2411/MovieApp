package com.example.movieapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.util.Converters

@Database(entities = [MovieHistory::class, VideoDownload::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
}