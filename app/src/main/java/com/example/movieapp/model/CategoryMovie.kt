package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class CategoryMovie(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)
