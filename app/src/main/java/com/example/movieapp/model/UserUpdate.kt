package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class UserUpdate(
    @SerializedName("name") val name: String,
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("password") val password: String
)