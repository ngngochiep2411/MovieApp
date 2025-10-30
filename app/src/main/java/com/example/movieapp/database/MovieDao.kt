package com.example.movieapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.model.PlayList
import com.example.movieapp.model.PlaylistVideoCrossRef
import com.example.movieapp.model.Video
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
    suspend fun getVideoDownload(slug: String?): VideoDownload

    @Query("DELETE FROM video_downloads WHERE slug = :slug ")
    suspend fun deleteFile(slug: String): Int


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayList(playList: PlayList): Long

    @Query("SELECT * FROM playlist")
    fun getAllPlayListsFlow(): Flow<List<PlayList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistVideoCrossRef(crossRef: PlaylistVideoCrossRef)

    @Transaction
    suspend fun addVideoToPlayLists(slug: String, playlistIds: List<Int>) {
        playlistIds.forEach { playlistId ->
            insertPlaylistVideoCrossRef(
                PlaylistVideoCrossRef(playlistId = playlistId, slug = slug)
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideo(video: Video)

    @Query("SELECT * FROM playlist")
    fun getAllPlayLists(): Flow<List<PlayList>>

    @Delete
    suspend fun delete(playList: PlayList)

    @Transaction
    @Query("""
        SELECT v.* FROM video v
        INNER JOIN playlist_video_crossref pv 
        ON v.slug = pv.slug
        WHERE pv.playlistId = :playlistId
    """)
     fun getVideosByPlayListId(playlistId: Int): Flow<List<Video>>

    @Query("DELETE FROM playlist_video_crossref WHERE slug = :slug AND playlistId = :playlistId")
    suspend fun deleteVideoFromPlaylist(slug: String, playlistId: Int): Int
}
