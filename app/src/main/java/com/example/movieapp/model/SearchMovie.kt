package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class SearchMovie(

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("status")
    val status: String? = null
)





