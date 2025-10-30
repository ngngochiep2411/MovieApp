package com.example.movieapp.ui.authen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val databaseManager: DatabaseManager,
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _message

    private val _login = MutableLiveData<Boolean>()
    val login: LiveData<Boolean> get() = _login

    fun register(email: String, password: String, userName: String) {
        viewModelScope.launch {
            mainRepository.register(email, password, userName).collect {
                if (it.success()) {
                    _message.value = it.message
                    _login.value = true
                } else {
                    _message.value = it.message
                }
            }
        }
    }
}