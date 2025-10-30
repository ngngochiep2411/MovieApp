package com.example.movieapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.model.Comment
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.model.User
import com.example.movieapp.model.UserDetail
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val mainRepository: MainRepository,
) : ViewModel() {


    private val _userDetail = MutableStateFlow<User?>(null)
    val userDetail: StateFlow<User?> = _userDetail

    private val _history = MutableStateFlow<List<MovieHistory>>(emptyList())
    val history: StateFlow<List<MovieHistory>> = _history

    init {
        viewModelScope.launch {
            launch {
                databaseManager.userDetail.collect {
                    _userDetail.value = it
                }
            }

            launch {
                databaseManager.getAllHistory().collect {
                    _history.value = it
                }
            }

        }

    }

    fun logOut() {
        viewModelScope.launch {
            databaseManager.logout()
        }


    }


}