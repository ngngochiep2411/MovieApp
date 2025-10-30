package com.example.movieapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.database.DatabaseManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val databaseManager: DatabaseManager,
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _message

    private val _login = MutableLiveData<Boolean>()
    val login: LiveData<Boolean> get() = _login

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun login(email: String, password: String) {
        _loading.value = true
        viewModelScope.launch {
            mainRepository.login(email, password).collect {
                _loading.value = false
                if (it.success()) {
                    if (it != null && it.data != null && it.data.user != null) {
                        databaseManager.saveUser(Gson().toJson(it.data.user))
                        _login.value = true
                    }
                } else {
                    _message.value = it.message
                }
            }
        }

    }
}