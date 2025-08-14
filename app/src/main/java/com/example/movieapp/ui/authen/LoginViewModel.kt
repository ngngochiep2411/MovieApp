package com.example.movieapp.ui.authen

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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            mainRepository.login(email, password).collect {
                _message.value = it.message
                _login.value = it.success()
                databaseManager.saveUser(Gson().toJson(it.data.user))
            }
        }

    }
}