package com.example.movieapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.example.movieapp.network.MovieApiService
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<ArrayList<Movie?>>(ArrayList())
    val movies: StateFlow<ArrayList<Movie?>> = _movies


    fun search(keyword: String) {
        viewModelScope.launch {
            repository.searchMovie(keyword).collect {
                _movies.value =
                    if (it is NetworkResult.Success) {
                        it.data.data?.items ?: ArrayList()
                    } else ArrayList()
            }
        }

    }
}