package com.example.movieapp.model

import com.example.movieapp.Constant
import com.google.gson.annotations.SerializedName

data class User(

    @field:SerializedName("avatar_url") val avatarUrl: String? = null,

    @field:SerializedName("updated_at") val updatedAt: String? = null,

    @field:SerializedName("name") val name: String = "",

    @field:SerializedName("created_at") val createdAt: String? = null,

    @field:SerializedName("email_verified_at") val emailVerifiedAt: String? = null,

    @field:SerializedName("id") val id: Int,

    @field:SerializedName("email") val email: String? = null
) {
    val avatar_url: String?
        get() = avatarUrl?.let { "${Constant.BASE_IMAGE_URL}$it" }
}