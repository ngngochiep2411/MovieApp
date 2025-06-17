package com.example.movieapp.ui.detailcategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieapp.model.Movie
import com.example.movieapp.model.SearchResultMovie
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class DetailCategoryViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val databaseManager: DatabaseManager,
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

    fun getData(type: String?) {
    }

    fun getMovie(type: Int?, category: String, country: String, year: String) = flow {
        currentPage = 1
        when (type) {
            0 -> {
                mainRepository.getSeriesMovie(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    currentPage = response?.data?.data?.params?.pagination?.currentPage!!
                    val list =
                        response.data.data.items
                    nextPage =
                        response.data.data.params.pagination.currentPage < response.data.data.params.pagination.totalPages
                    emit(list)
                    _isLoading.value = false
                }
            }

            1 -> {
                mainRepository.getMovies(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    currentPage = response?.data?.data?.params?.pagination?.currentPage!!
                    val list =
                        response.data.data.items
                    nextPage =
                        response.data.data.params.pagination.currentPage < response.data.data.params.pagination.totalPages
                    emit(list)
                    _isLoading.value = false
                }
            }

            2 -> {
                mainRepository.getTvShows(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    currentPage = response?.data?.data?.params?.pagination?.currentPage!!
                    val list =
                        response.data.data.items
                    nextPage =
                        response.data.data.params.pagination.currentPage < response.data.data.params.pagination.totalPages
                    emit(list)
                    _isLoading.value = false
                }
            }

            3 -> {
                mainRepository.getCartoonMovie(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    currentPage = response?.data?.data?.params?.pagination?.currentPage!!
                    val list =
                        response.data.data.items
                    nextPage =
                        response.data.data.params.pagination.currentPage < response.data.data.params.pagination.totalPages
                    emit(list)
                    _isLoading.value = false
                }
            }

            4 -> {
                mainRepository.getVietSub(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    currentPage = response?.data?.data?.params?.pagination?.currentPage!!
                    val list =
                        response.data.data.items
                    nextPage =
                        response.data.data.params.pagination.currentPage < response.data.data.params.pagination.totalPages
                    emit(list)
                    _isLoading.value = false
                }
            }

            5 -> {
                mainRepository.getThuyetMinh(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    currentPage = response?.data?.data?.params?.pagination?.currentPage!!
                    val list =
                        response.data.data.items
                    nextPage =
                        response.data.data.params.pagination.currentPage < response.data.data.params.pagination.totalPages
                    emit(list)
                    _isLoading.value = false
                }
            }

            6 -> {
                mainRepository.getLongTieng(currentPage, category, country, year).collect {
                    val response = it as? NetworkResult.Success
                    currentPage = response?.data?.data?.params?.pagination?.currentPage!!
                    val list =
                        response.data.data.items
                    nextPage =
                        response.data.data.params.pagination.currentPage < response.data.data.params.pagination.totalPages
                    emit(list)
                    _isLoading.value = false
                }
            }
        }
    }
}