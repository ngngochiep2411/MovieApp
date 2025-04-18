package com.example.movieapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieapp.model.UserDetail
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private var _videoIndex = MutableStateFlow(-1)
    val videoIndex: StateFlow<Int> = _videoIndex

    private val _login = MutableStateFlow(false)
    val login: MutableStateFlow<Boolean> get() = _login



    fun setLoginSuccess() {
        _login.value = true
    }

    fun setUserDetail() {

    }


    fun changeVideoIndex(videoIndex: Int) {
        _videoIndex.value = videoIndex
    }




}