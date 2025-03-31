package com.example.movieapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.DataStoreManager
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject


class DetailUserViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dataStoreManager: DataStoreManager,
) : ViewModel(
) {

    fun update(user_id: String, file: File?, name: String, password: String) {
        viewModelScope.launch {
            val requestFile: RequestBody =
                file!!.asRequestBody(MultipartBody.FORM)
            val avatar: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            mainRepository.updateUser(
                user_id = user_id.toRequestBody(MultipartBody.FORM),
                avatar_url = avatar,
                name = name.toRequestBody(MultipartBody.FORM),
                password = password.toRequestBody(MultipartBody.FORM)
            ).collect {

            }
        }

    }

}