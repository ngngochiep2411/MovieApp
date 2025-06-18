package com.example.movieapp.ui.detailmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailMovieViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    val movieDao: MovieDao
) : ViewModel() {

    private val _videoUrls = MutableStateFlow<List<String?>?>(null)
    val videoUrls: StateFlow<List<String?>?> = _videoUrls

    private val _detailMovie = MutableStateFlow<DetailMovie?>(null)
    val detailMovie: StateFlow<DetailMovie?> = _detailMovie

    private val _lastWatchedEpisode = MutableStateFlow<Int?>(null)
    val lastWatchedEpisode: StateFlow<Int?> = _lastWatchedEpisode

    private val _watchedAt = MutableStateFlow<Long?>(null)
    val watchedAt: StateFlow<Long?> = _watchedAt

    fun getData(slug: String) {
        viewModelScope.launch {
            val lastWatchedEpisode = movieDao.getWatchedEpisodes(slug).firstOrNull()
            _lastWatchedEpisode.value = lastWatchedEpisode

            val watchedAt = movieDao.getWatchedAt(slug).firstOrNull()
            _watchedAt.value = watchedAt

            mainRepository.getDetailMovie(name = slug).collect { response ->
                if (response is NetworkResult.Success) {
                    _detailMovie.value = response.data
                    val urls = response.data.episodes?.get(0)?.serverData?.map {
                        it.linkM3u8
                    } ?: emptyList()
                    _videoUrls.value = urls
                }
            }
        }
    }

    fun saveMovieWatched(thumbUrl: String?, slug: String?, name: String?) {
        viewModelScope.launch {
            val movie = MovieHistory(
                thumbUrl = thumbUrl,
                name = name,
                slug = slug.toString()
            )
            movieDao.insertHistory(movie)
        }
    }

    fun updateEpisode(slug: String, episode: Int) {
        viewModelScope.launch {
            movieDao.updateEpisode(slug, episode)
        }
    }

    fun updateWatchedAt(slug: String, watchedAt: Long) {
        viewModelScope.launch {
            movieDao.updateWatchedAt(watchedAt, slug)
        }
    }


}