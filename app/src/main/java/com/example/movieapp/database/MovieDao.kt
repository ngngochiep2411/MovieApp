package com.example.movieapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.model.VideoDownload
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
    fun getWatchedAt(slug: String): Flow<Long?>

    @Query("UPDATE movie_view_history SET episode = :episode WHERE slug = :slug")
    suspend fun updateEpisode(slug: String?, episode: Int)

    @Query("UPDATE movie_view_history SET watchedAt = :watchedAt WHERE slug = :slug")
    suspend fun updateWatchedAt(watchedAt: Long, slug: String)

    @Query("DELETE FROM movie_view_history WHERE slug IN (:slugs)")
    suspend fun deleteMovies(slugs: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: VideoDownload): Long

    @Query("SELECT * FROM video_downloads")
    fun getAllDownloads(): Flow<List<VideoDownload>>

    @Query("SELECT * FROM video_downloads WHERE slug = :slug LIMIT 1")
    suspend fun getVideoDownload(slug: String): VideoDownload
}
