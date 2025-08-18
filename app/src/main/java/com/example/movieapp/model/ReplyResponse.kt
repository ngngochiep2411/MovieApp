package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class ReplyResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("comment_id") val comment_id: Int,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("updated_at") val updated_at: String,
    @SerializedName("user") val user: User,
    @SerializedName("reply_user") val reply_user: User?,
    @SerializedName("image") val image: String = "",
)
