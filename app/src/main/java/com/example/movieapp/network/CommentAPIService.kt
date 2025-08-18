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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface CommentAPIService {

    @GET("comments")
    suspend fun getComment(
        @Query("video_id") video_id: String?,
        @Query("page") page: Int,
    ): BaseResponse<List<Comment>>

    @POST("comment")
    @Multipart
    suspend fun comment(
        @Part("content") content: RequestBody,
        @Part("video_id") videoId: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseResponse<CommentResponse>

    @POST("reply")
    @Multipart
    suspend fun reply(
//        @Body comment: ReplyData,
        @Part("user_id") user_id: RequestBody,
        @Part("content") content: RequestBody,
        @Part("comment_id") comment_id: RequestBody,
        @Part("reply_user_id") reply_user_id: RequestBody,
        @Part image: MultipartBody.Part?
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

    @Multipart
    @POST("user/update")
    suspend fun updateUser(
        @Part("user_id") user_id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("password") password: RequestBody,
        @Part avatar_url: MultipartBody.Part?
    ): BaseResponse<Any>
}