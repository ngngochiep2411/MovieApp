package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class PaginationV2(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("lastPage")
    val lastPage: Int,
    @SerializedName("perPage")
    val perPage: Int,
    @SerializedName("nextPage")
    var nextPage: Boolean,
    @SerializedName("total")
    val total: Int,
    @SerializedName("count")
    val count: Int,
)
