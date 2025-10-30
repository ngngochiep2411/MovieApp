package com.example.movieapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.database.MovieDao
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val databaseManager: DatabaseManager,
    private val movieDao: MovieDao
) : ViewModel() {

    enum class PlayType {
        LONG_TIENG,
        VIETSUB
    }

    private var _videoIndex = MutableStateFlow(-1)
    val videoIndex: StateFlow<Int> = _videoIndex

    private val _typePlay = MutableStateFlow(PlayType.LONG_TIENG) // giá trị mặc định
    val typePlay: StateFlow<PlayType> = _typePlay

    fun setTypePlay(type: PlayType) {
        _typePlay.value = type
    }

    private val _login = MutableStateFlow(false)
    val login: MutableStateFlow<Boolean> get() = _login


    fun setLoginSuccess() {
        _login.value = true
    }

    fun setUserDetail() {

    }


    fun changeVideoIndex(videoIndex: Int) {
        _videoIndex.value = videoIndex
    }

    fun updateEpisode(slug: String?, episode: Int) {
        viewModelScope.launch {
            movieDao.updateEpisode(slug, episode)
        }
    }

    fun updateWatchedAt(slug: String, current: Long) {
        viewModelScope.launch {
            movieDao.updateWatchedAt(current, slug)
        }
    }

}