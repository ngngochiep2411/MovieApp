package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class Pagination(

    @field:SerializedName("totalItems")
    val totalItems: Int,

    @field:SerializedName("totalPages")
    val totalPages: Int,

    @field:SerializedName("totalItemsPerPage")
    val totalItemsPerPage: Int,

    @field:SerializedName("currentPage")
    val currentPage: Int = 1
)