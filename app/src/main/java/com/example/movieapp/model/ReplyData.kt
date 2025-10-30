package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class ReplyData(
    @SerializedName("user_id")
    val user_id: Int?,
    @SerializedName("content")
    val content: String,
    @SerializedName("comment_id")
    val comment_id: Int?,
    @SerializedName("reply_user_id")
    val reply_user_id: Int?,
)
