package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class GetReply(
    @SerializedName("video_id")
    val video_id: String?,
    @SerializedName("comment_id")
    val comment_id: Int,
//    @SerializedName("page")
//    val page: Int
)
