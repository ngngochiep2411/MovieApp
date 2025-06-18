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

    @Query("SELECT episode FROM movie_view_history WHERE slug = :slug")
    fun getWatchedEpisodes(slug: String): Flow<Int>

    @Query("SELECT watchedAt FROM movie_view_history WHERE slug = :slug")
    fun getWatchedAt(slug: String): Flow<Long>

    @Query("UPDATE movie_view_history SET episode = :episode WHERE slug = :slug")
    suspend fun updateEpisode(slug: String, episode: Int)

    @Query("UPDATE movie_view_history SET watchedAt = :watchedAt WHERE slug = :slug")
    suspend fun updateWatchedAt(watchedAt: Long, slug: String)
}
