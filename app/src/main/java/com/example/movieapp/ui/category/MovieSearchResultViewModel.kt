package com.example.movieapp.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.example.movieapp.model.SearchResultMovie
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieSearchResultViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private var currentPage: Int = 1
    var nextPage = false
    private val _isLoading = MutableLiveData(false)

    var isLoading = _isLoading

    private val _movie = MutableStateFlow<SearchResultMovie?>(null)
    val movie: StateFlow<SearchResultMovie?> = _movie

    private val _moreMovie = MutableStateFlow<ArrayList<Movie?>?>(ArrayList())
    val moreMovie: StateFlow<ArrayList<Movie?>?> = _moreMovie

    private var isLoadMore = false

    private var currentJob: Job? = null


    fun getData(type: Int?, category: String, country: String, year: String) {
        currentJob = viewModelScope.launch {
            _isLoading.value = true
            combine(
                mainRepository.getCategory(),
                mainRepository.getCountry(),
                getMovie(type, category, country, year)
            ) { categoryFlow, country, moviesFlow ->
                SearchResultMovie(
                    country = country,
                    category = categoryFlow,
                    movies = moviesFlow
                )
            }.collect {
                _isLoading.value = false
                _movie.value = it

            }

        }


    }

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }

    fun getMoreData(type: Int?, category: String, country: String, year: String) {
        if (!isLoadMore) {
            isLoadMore = true
            viewModelScope.launch {

                when (type) {
                    0 -> {
                        mainRepository.getSeriesMovie(currentPage + 1, category, country, year)
                            .collect {
                                val response = (it as? NetworkResult.Success)
                                currentPage =
                                    response?.data?.data?.params?.pagination?.currentPage ?: 1
                                val total =
                                    response?.data?.data?.params?.pagination?.totalPages ?: 1
                                val list = response?.data?.data?.items ?: ArrayList()

                                _moreMovie.value = list
                                nextPage = currentPage < total

                                isLoadMore = false

                            }
                    }

                    1 -> {
                        mainRepository.getMovies(currentPage + 1, category, country, year).collect {
                            val response = (it as? NetworkResult.Success)
                            currentPage = response?.data?.data?.params?.pagination?.currentPage ?: 1
                            val total = response?.data?.data?.params?.pagination?.totalPages ?: 1
                            val list = response?.data?.data?.items ?: ArrayList()

                            _moreMovie.value = list
                            nextPage = currentPage < total
                            isLoadMore = false
                        }
                    }

                    2 -> {
                        mainRepository.getTvShows(currentPage + 1, category, country, year)
                            .collect {
                                val response = (it as? NetworkResult.Success)
                                currentPage =
                                    response?.data?.data?.params?.pagination?.currentPage ?: 1
                                val total =
                                    response?.data?.data?.params?.pagination?.totalPages ?: 1
                                val list = response?.data?.data?.items ?: ArrayList()

                                _moreMovie.value = list
                                nextPage = currentPage < total
                                isLoadMore = false
                            }
                    }

                    3 -> {
                        mainRepository.getCartoonMovie(currentPage + 1, category, country, year)
                            .collect {
                                val response = (it as? NetworkResult.Success)
                                currentPage =
                                    response?.data?.data?.params?.pagination?.currentPage ?: 1
                                val total =
                                    response?.data?.data?.params?.pagination?.totalPages ?: 1
                                val list = response?.data?.data?.items ?: ArrayList()

                                _moreMovie.value = list
                                nextPage = currentPage < total
                                isLoadMore = false
                            }
                    }
                }
            }
        }

    }

    fun getMovie(type: Int?, category: String, country: String, year: String) = flow {
        currentPage = 1
        when (type) {
            0 -> {
                mainRepository.getSeriesMovie(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    val currentPage = response?.data?.data?.params?.pagination?.currentPage ?: 1
                    val lastPage = response?.data?.data?.params?.pagination?.totalPages ?: 1
                    val list =
                        response?.data?.data?.items ?: ArrayList()
                    nextPage = currentPage < lastPage
                    emit(list)
                    _isLoading.value = false
                }
            }

            1 -> {
                mainRepository.getMovies(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    val currentPage = response?.data?.data?.params?.pagination?.currentPage ?: 1
                    val lastPage = response?.data?.data?.params?.pagination?.totalPages ?: 1
                    val list =
                        response?.data?.data?.items ?: ArrayList()
                    nextPage = currentPage < lastPage
                    emit(list)
                    _isLoading.value = false
                }
            }

            2 -> {
                mainRepository.getTvShows(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    val currentPage = response?.data?.data?.params?.pagination?.currentPage ?: 1
                    val lastPage = response?.data?.data?.params?.pagination?.totalPages ?: 1
                    val list =
                        response?.data?.data?.items ?: ArrayList()
                    nextPage = currentPage < lastPage
                    emit(list)
                    _isLoading.value = false
                }
            }

            3 -> {
                mainRepository.getCartoonMovie(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    val currentPage = response?.data?.data?.params?.pagination?.currentPage ?: 1
                    val lastPage = response?.data?.data?.params?.pagination?.totalPages ?: 1
                    val list =
                        response?.data?.data?.items ?: ArrayList()
                    nextPage = currentPage < lastPage
                    emit(list)
                    _isLoading.value = false
                }
            }

            4 -> {
                mainRepository.getVietSub(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    val currentPage = response?.data?.data?.params?.pagination?.currentPage ?: 1
                    val lastPage = response?.data?.data?.params?.pagination?.totalPages ?: 1
                    val list =
                        response?.data?.data?.items ?: ArrayList()
                    nextPage = currentPage < lastPage
                    emit(list)
                    _isLoading.value = false
                }
            }

            5 -> {
                mainRepository.getThuyetMinh(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    val currentPage = response?.data?.data?.params?.pagination?.currentPage ?: 1
                    val lastPage = response?.data?.data?.params?.pagination?.totalPages ?: 1
                    val list =
                        response?.data?.data?.items ?: ArrayList()
                    nextPage = currentPage < lastPage
                    emit(list)
                    _isLoading.value = false
                }
            }

            6 -> {
                mainRepository.getLongTieng(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    val currentPage = response?.data?.data?.params?.pagination?.currentPage ?: 1
                    val lastPage = response?.data?.data?.params?.pagination?.totalPages ?: 1
                    val list =
                        response?.data?.data?.items ?: ArrayList()
                    nextPage = currentPage < lastPage
                    emit(list)
                    _isLoading.value = false
                }
            }
        }
    }
}