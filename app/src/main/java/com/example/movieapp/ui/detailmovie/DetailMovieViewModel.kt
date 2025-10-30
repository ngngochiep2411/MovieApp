package com.example.movieapp.ui.detailmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailMovieViewModel @Inject constructor(
    private val mainRepository: MainRepository, val movieDao: MovieDao
) : ViewModel() {

    //vietsub
    private val _videoUrls = MutableStateFlow<VideoURL?>(null)
    val videoUrls: StateFlow<VideoURL?> = _videoUrls


    private val _detailMovie = MutableStateFlow<DetailMovie?>(null)
    val detailMovie: StateFlow<DetailMovie?> = _detailMovie

    private val _lastWatchedEpisode = MutableStateFlow<Int?>(null)
    val lastWatchedEpisode: StateFlow<Int?> = _lastWatchedEpisode

    private val _watchedAt = MutableStateFlow<Long?>(null)
    val watchedAt: StateFlow<Long?> = _watchedAt


    fun getData(slug: String) {
        viewModelScope.launch {
            getLastWatchedEpisode(slug)
            getWatchedAt(slug)
            getMovie(slug)
        }
    }

    suspend fun getWatchedAt(slug: String) {
        val watchedAt = withContext(Dispatchers.IO) {
            movieDao.getWatchedAt(slug).firstOrNull()
        }
        _watchedAt.value = watchedAt
    }

    suspend fun getLastWatchedEpisode(slug: String) {
        val episode = withContext(Dispatchers.IO) {
            movieDao.getWatchedEpisodes(slug).firstOrNull()
        }
        _lastWatchedEpisode.value = episode
    }

    suspend fun getMovie(slug: String) {
        mainRepository.getDetailMovie(name = slug).collect { response ->
            if (response is NetworkResult.Success) {
                _detailMovie.value = response.data
                val episodes = response.data.episodes ?: emptyList()
                var vietsub = emptyList<String>()
                var longTieng = emptyList<String>()

                episodes.take(2).forEach { ep ->
                    when (ep.serverName) {
                        "#Hà Nội (Vietsub)" -> {
                            vietsub = ep.serverData.map { it.linkM3u8 }
                        }

                        "#Hà Nội (Thuyết Minh)" -> {
                            longTieng = ep.serverData.map { it.linkM3u8 }
                        }
                    }
                }
                _videoUrls.value = VideoURL(
                    videoLongTieng = longTieng,
                    videoVietSub = vietsub
                )
            }
        }
    }

    fun saveMovieWatched(
        thumbUrl: String?, slug: String?, name: String?, duration: Long, total: String
    ) {
        viewModelScope.launch {
            val movie = MovieHistory(
                thumbUrl = thumbUrl,
                name = name,
                slug = slug.toString(),
                duration = duration,
                total = total
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

data class VideoURL(
    val videoLongTieng: List<String>,
    val videoVietSub: List<String>
)