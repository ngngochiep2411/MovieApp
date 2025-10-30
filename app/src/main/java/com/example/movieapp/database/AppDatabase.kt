package com.example.movieapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.movieapp.model.Favorite
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.model.PlayList
import com.example.movieapp.model.PlaylistVideoCrossRef
import com.example.movieapp.model.Video
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.util.Converters

@Database(
    entities = [MovieHistory::class, VideoDownload::class, Favorite::class, PlayList::class, PlaylistVideoCrossRef::class, Video::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
}