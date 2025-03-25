package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class CountryMovie(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)
