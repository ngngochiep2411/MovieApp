package com.example.movieapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.CartoonMovie
import com.example.movieapp.model.HomeData
import com.example.movieapp.model.Movies
import com.example.movieapp.model.NewMovie
import com.example.movieapp.model.SeriesMovie
import com.example.movieapp.model.TvShowsMovie
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _movieResponse: MutableLiveData<NetworkResult<HomeData>> = MutableLiveData()
    val movieResponse: LiveData<NetworkResult<HomeData>>
        get() = _movieResponse

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun getData() {
        _isLoading.value = true
        viewModelScope.launch {
//            delay(10000L)
            val flows = listOf(
                mainRepository.getMovies(),
                mainRepository.getTvShows(),
                mainRepository.getSeriesMovie(),
                mainRepository.getCartoonMovie(),
                mainRepository.getNewMovie(),
                mainRepository.getVietSub(),
                mainRepository.getThuyetMinh(),
                mainRepository.getLongTieng()
            )

            combine(flows) { results ->
                HomeData(
                    movies = (results[0] as? NetworkResult.Success)?.data as? Movies,
                    tvShowsMovie = (results[1] as? NetworkResult.Success)?.data as? TvShowsMovie,
                    seriesMovie = (results[2] as? NetworkResult.Success)?.data as? SeriesMovie,
                    cartoonMovie = (results[3] as? NetworkResult.Success)?.data as? CartoonMovie,
                    newMovie = (results[4] as? NetworkResult.Success)?.data as? NewMovie,
                    viet_sub = (results[5] as? NetworkResult.Success)?.data as? Movies,
                    thuyet_minh = (results[6] as? NetworkResult.Success)?.data as? Movies,
                    long_tieng = (results[7] as? NetworkResult.Success)?.data as? Movies
                )
            }.collect { homeData ->
                _movieResponse.postValue(NetworkResult.Success(homeData))
                _isLoading.value = false
            }
        }
    }

}
