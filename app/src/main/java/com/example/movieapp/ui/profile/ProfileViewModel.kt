package com.example.movieapp.ui.profile

import androidx.lifecycle.ViewModel
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.model.Comment
import com.example.movieapp.model.User
import com.example.movieapp.model.UserDetail
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val mainRepository: MainRepository,
) : ViewModel() {


    val userDetail = databaseManager.userDetail



}