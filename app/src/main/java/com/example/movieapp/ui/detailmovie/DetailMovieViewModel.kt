package com.example.movieapp.ui.detailmovie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailMovieViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _videoUrls = MutableStateFlow<List<String?>?>(null)
    val videoUrls: StateFlow<List<String?>?> = _videoUrls

    private val _videoUrl = MutableStateFlow<String?>(null)
    val videoUrl: StateFlow<String?> = _videoUrl


    private val _currentVideoIndex = MutableLiveData<Int>(0)
    val currentVideoIndex: LiveData<Int> = _currentVideoIndex

    private val _detailMovie = MutableStateFlow<DetailMovie?>(null)
    val detailMovie: StateFlow<DetailMovie?> = _detailMovie

    fun getData(movieName: String) {
        viewModelScope.launch {
            mainRepository.getDetailMovie(name = movieName).collect { response ->
                if (response is NetworkResult.Success) {
                    _detailMovie.value = response.data
                    val urls = response.data.episodes?.get(0)?.serverData?.map {
                        it.linkM3u8
                    } ?: emptyList()
                    _videoUrls.value = urls
                    _videoUrl.value = urls[0]
                }
            }
        }
    }


}