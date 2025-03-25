package com.example.movieapp.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.BaseResponse
import com.example.movieapp.model.UserDetail
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {


    fun register(email: String, password: String, userName: String) =
        flow {
            viewModelScope.launch {
                mainRepository.register(email, password, userName).collect {
                    emit(it)
                }
            }
        }
}