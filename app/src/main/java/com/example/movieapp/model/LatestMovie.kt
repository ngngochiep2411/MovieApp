package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class LatestMovie(

    @field:SerializedName("pagination")
    val pagination: Pagination? = null,

    @field:SerializedName("items")
    val items: List<Movie>? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
)

