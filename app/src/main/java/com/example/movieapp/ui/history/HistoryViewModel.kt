package com.example.movieapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val databaseManager: DatabaseManager,
    private val dao: MovieDao
) : ViewModel() {

    private val _history = MutableStateFlow<List<MovieHistory>>(emptyList())
    val history: StateFlow<List<MovieHistory>> = _history

    private val _movieList = dao.getAllMovies()
    val movieList: Flow<List<MovieHistory>> = _movieList

    fun getHistory() {
        viewModelScope.launch {
            dao.getAllMovies().collect {
                _history.value = it
            }
        }
    }

    fun deleteMovies(listDelete: ArrayList<String>) {
        viewModelScope.launch {
            dao.deleteMovies(listDelete)
        }
    }
}