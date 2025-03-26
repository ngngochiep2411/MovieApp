package com.example.movieapp.ui.auth.login

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.DataStoreManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dataStoreManager: DataStoreManager,


    ) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _message

    private val _login = MutableLiveData<Boolean>()
    val login: LiveData<Boolean> get() = _login

    fun login(email: String, password: String) {
        viewModelScope.launch {
            mainRepository.login(email, password).collect {
                if (it.success()) {
                    _login.value = true
                    dataStoreManager.saveUserDetail(Gson().toJson(it.data))
                } else {
                    _login.value = false
                    _message.value = it.message
                }
            }
        }

    }
}