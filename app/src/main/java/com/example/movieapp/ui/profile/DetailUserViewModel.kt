package com.example.movieapp.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.database.DatabaseManager

import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class DetailUserViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val databaseManager: DatabaseManager,
) : ViewModel(
) {

    private val _message = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun update(user_id: String, file: MultipartBody.Part?, name: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            mainRepository.updateUser(
                user_id = createRequestBody(user_id),
                avatar_url = file,
                name = createRequestBody(name),
                password = createRequestBody(password)
            ).collect {
                _isLoading.value = false
                _message.value = it.message
                if (it.success()) {
                    databaseManager.saveUser(Gson().toJson(it.user))
                }
            }
        }
    }
}


private fun createRequestBody(data: String): RequestBody {
    return data.toRequestBody("text/plain;charset=utf-8".toMediaType())
}