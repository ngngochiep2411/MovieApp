package com.example.movieapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.model.MovieHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(movie: MovieHistory)

    @Query("SELECT * FROM movie_view_history")
    fun getAllMovies(): Flow<List<MovieHistory>>
}
