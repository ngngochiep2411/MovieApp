package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class CommentResponse(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("content")
    val content: String,

    @field:SerializedName("video_id")
    val videoId: String? = null,

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("image")
    val image: String = "",
)
