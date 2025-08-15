package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class Comment(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("pagination")
    val paginationReply: PaginationV2,

    @field:SerializedName("user_id")
    val userId: Int,

    @field:SerializedName("replys")
    val replys: List<Reply>,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("content")
    val content: String,

    @field:SerializedName("video_id")
    val videoId: String? = null,

    @field:SerializedName("image")
    val image: String = "",
) {
    fun getUserName(): String {
        if (user != null && user.name.isNotEmpty()) {
            return user.name
        }
        return ""
    }
}