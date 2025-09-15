package com.example.movieapp.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.network.CommentAPIService
import com.example.movieapp.network.MovieApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoDownloadViewModel @Inject constructor(
    private val movieApiService: MovieApiService,
    private val commentApiService: CommentAPIService,
    private val movieDao: MovieDao
) : ViewModel() {

    private val _videoDownload = MutableStateFlow<List<VideoDownload>>(emptyList())
    val videoDownload: StateFlow<List<VideoDownload>> = _videoDownload

    fun getVideoDownload() {
        viewModelScope.launch {
            movieDao.getAllDownloads().collect {
                _videoDownload.value = it
            }
        }

    }

}