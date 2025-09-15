package com.example.movieapp.ui.listvideo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.Comment
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.network.CommentAPIService
import com.example.movieapp.network.MovieApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                    Log.d("RoomDB", "Save thành công với rowId = $rowId")
                } else {
                    Log.w("RoomDB", "Save thất bại (rowId = $rowId)")
                }
            } catch (e: Exception) {
                Log.e("RoomDB", "Lỗi khi save: ${e.message}", e)
            }
        }
    }
}