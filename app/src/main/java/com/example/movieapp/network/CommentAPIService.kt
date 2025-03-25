package com.example.movieapp.network

import com.example.movieapp.model.BaseResponse
import com.example.movieapp.model.Comment
import com.example.movieapp.model.CommentData
import com.example.movieapp.model.CommentResponse
import com.example.movieapp.model.GetReply
import com.example.movieapp.model.Login
import com.example.movieapp.model.Register
import com.example.movieapp.model.Reply
import com.example.movieapp.model.ReplyData
import com.example.movieapp.model.ReplyResponse
import com.example.movieapp.model.UserDetail
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CommentAPIService {

    @GET("comments")
    suspend fun getComment(
        @Query("video_id") video_id: String?,
        @Query("page") page: Int,
    ): BaseResponse<List<Comment>>

    @POST("comment")
    suspend fun comment(
        @Body comment: CommentData
    ): BaseResponse<CommentResponse>

    @POST("reply")
    suspend fun reply(
        @Body comment: ReplyData
    ): BaseResponse<ReplyResponse>

    @POST("login")
    suspend fun login(
        @Body login: Login
    ): BaseResponse<UserDetail>

    @POST("register")
    suspend fun register(
        @Body login: Register
    ): BaseResponse<UserDetail>

    @GET("replys")
    suspend fun getReply(
        @Query("video_id") video_id: String?,
        @Query("comment_id") comment_id: Int?,
        @Query("page") page: Int
    ): BaseResponse<List<Reply>>
}