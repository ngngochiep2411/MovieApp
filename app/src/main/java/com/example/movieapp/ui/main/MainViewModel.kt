package com.example.movieapp.ui.main

import androidx.lifecycle.ViewModel
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {


    fun getData() {

    }
}