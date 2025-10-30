package com.example.movieapp.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.PlayList
import com.example.movieapp.model.Video
import com.example.movieapp.network.CommentAPIService
import com.example.movieapp.network.MovieApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(
    private val movieApiService: MovieApiService,
    private val commentApiService: CommentAPIService,
    private val movieDao: MovieDao
) : ViewModel(

) {
    val playLists: Flow<List<PlayList>> = movieDao.getAllPlayLists()
    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos

    fun loadVideosForPlaylist(playlistId: Int) {
        viewModelScope.launch {
            movieDao.getVideosByPlayListId(playlistId)
                .collectLatest { list ->
                    _videos.value = list
                }
        }
    }

    fun deleteVideo(slug: String, playListId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val rowsDeleted = movieDao.deleteVideoFromPlaylist(slug, playListId)
                if (rowsDeleted > 0) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }

        }
    }

}