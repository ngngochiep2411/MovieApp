package com.example.movieapp.ui.auth.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _message = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _message

    private val _register = MutableLiveData<Boolean>()
    val register: LiveData<Boolean> get() = _register

    fun register(email: String, password: String, userName: String) =
        viewModelScope.launch {
            mainRepository.register(email, password, userName).collect {
                if (it.success()) {
                    _register.value = true
                    _message.value = it.message
                } else {
                    _message.value = it.message
                    _register.value = false
                }

            }
        }
}