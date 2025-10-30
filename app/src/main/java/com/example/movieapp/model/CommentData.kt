package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class CommentData(
    @SerializedName("content")
    val content: String,
    @SerializedName("video_id")
    val videoName: String,
    @SerializedName("user_id")
    val userId: Int?,
)
