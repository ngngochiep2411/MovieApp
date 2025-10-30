package com.example.movieapp.ui.download

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.internal.lifecycle.HiltViewModelMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlayVideoDownloadViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository,
    private val databaseManager: DatabaseManager,
    private val movieDao: MovieDao
) : AndroidViewModel(application) {

    val _videos = MutableLiveData<List<String>>()
    val videos: LiveData<List<String>> get() = _videos

    val _position = MutableLiveData<Int>(0)
    val position: LiveData<Int> get() = _position

    val _videoDownload = MutableLiveData<VideoDownload>()
    val videoDownload: LiveData<VideoDownload> get() = _videoDownload

    fun getVideoDownload(slug: String?) {
        viewModelScope.launch {
            val video = movieDao.getVideoDownload(slug)
            _videoDownload.value = video
        }
    }

    fun getVideo(slug: String?) {

        val movieDir = File(
            getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            slug
        )
        if (!movieDir.exists() || !movieDir.isDirectory) {
            _videos.value = emptyList()
            return
        } else {
            val mp4Files = movieDir.listFiles { file ->
                file.isFile && file.extension.equals("mp4", ignoreCase = true)
            }?.map { it.absolutePath } ?: emptyList()
            _videos.value = mp4Files
        }

    }

    fun updatePosition(position: Int) {
        _position.value = position
    }

    fun deleteFile(
        file: File,
        position: Int,
        onSuccess: (position: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (file.exists()) {
                file.delete()
            }
            onSuccess(position)
        }

    }
}