package com.example.movieapp.model

import com.google.gson.annotations.SerializedName


data class Reply(

    @SerializedName("pagination")
    val paginationReply: PaginationV2,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("comment_id")
    val commentId: Int,

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("reply_user")
    val reply_user: User? = null,

    @field:SerializedName("content")
    val content: String,

    @SerializedName("image")
    val image: String,

    ) {
    fun getUserName(): String {
        if (user != null && user.name.isNotEmpty()) {
            return user.name
        }
        return ""
    }
}