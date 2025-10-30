package com.example.movieapp.util

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Failure<T>(val errorMessage: String?) : NetworkResult<T>()
}