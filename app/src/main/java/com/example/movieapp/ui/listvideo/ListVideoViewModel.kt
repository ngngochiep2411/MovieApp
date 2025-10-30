package com.example.movieapp.ui.listvideo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.Comment
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.PlayList
import com.example.movieapp.model.Video
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.network.CommentAPIService
import com.example.movieapp.network.MovieApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListVideoViewModel @Inject constructor(
    private val movieApiService: MovieApiService,
    private val commentApiService: CommentAPIService,
    private val movieDao: MovieDao
) : ViewModel() {

    fun saveVideo(download: VideoDownload) {
        viewModelScope.launch {
            try {
                val rowId = movieDao.insert(video = download)
                if (rowId > 0) {
                } else {
                }
            } catch (e: Exception) {
                Log.e("RoomDB", "Lỗi khi save: ${e.message}", e)
            }
        }
    }

    fun createPlayList(name: String, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val id = movieDao.insertPlayList(
                    PlayList(playListName = name)
                )
                onResult(id)
            } catch (e: Exception) {
                onResult(-1)
            }
        }
    }

    fun addToPlayList(playlistIds: List<Int>, slug: String, detailMovie: DetailMovie) {
        viewModelScope.launch {
            val video = Video(slug = slug, detailMovie = detailMovie)
            movieDao.insertVideo(video)
            movieDao.addVideoToPlayLists(slug, playlistIds)
        }
    }

    val allPlayLists: StateFlow<List<PlayList>> =
        movieDao.getAllPlayListsFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                emptyList()
            )

}