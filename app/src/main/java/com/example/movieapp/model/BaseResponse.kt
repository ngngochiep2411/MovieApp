package com.example.movieapp.model

data class BaseResponse<T>(
    val error: Int,
    val message: String,
    val data: T,
    val pagination: PaginationV2
) {
    fun success(): Boolean {
        return error == 0
    }
}