package com.example.movieapp.ui.comment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.BaseResponse
import com.example.movieapp.model.Comment
import com.example.movieapp.model.CommentData
import com.example.movieapp.model.CommentResponse
import com.example.movieapp.model.Reply
import com.example.movieapp.model.ReplyData
import com.example.movieapp.model.ReplyResponse
import com.example.movieapp.model.User
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.database.DatabaseManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val mainRepository: MainRepository, private val databaseManager: DatabaseManager
) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comment>?>(null)
    val comments: StateFlow<List<Comment>?> = _comments


    private val _moreComments = MutableStateFlow<List<Comment>?>(null)
    val moreComments: StateFlow<List<Comment>?> = _moreComments

    private var currentPage = 1
    var nextPage = false

    private val _userDetail = MutableStateFlow<User?>(null)
    val userDetail: StateFlow<User?> = _userDetail

    private var isLoadMore = false

    init {
        viewModelScope.launch {
            databaseManager.userDetail.collect {
                _userDetail.value = it
            }
        }

    }


    fun getComment(videoName: String?) {
        viewModelScope.launch {
            Log.d("testing", "getComment $currentPage")
            mainRepository.getComment(videoName, currentPage).collect {
                currentPage = it.pagination.currentPage
                nextPage = it.pagination.nextPage
                _comments.value = it.data
            }
        }

    }

    fun getMoreComment(videoName: String?) {
        if (!isLoadMore) {
            viewModelScope.launch {
                Log.d("testing", "getMoreComment $currentPage")
                mainRepository.getComment(videoName, currentPage + 1).collect {
                    currentPage = it.pagination.currentPage
                    nextPage = it.pagination.nextPage
                    _moreComments.value = it.data
                }
            }
        }
    }

    fun comment(comment: CommentData, imagePart: MultipartBody.Part?) =
        flow {
            val contentBody = comment.content.toRequestBody("text/plain".toMediaType())
            val videoIdBody = comment.videoName.toRequestBody("text/plain".toMediaType())
            val userIdBody =
                (comment.userId?.toString() ?: "").toRequestBody("text/plain".toMediaType())
            mainRepository.comment(
                video_id = videoIdBody,
                content = contentBody,
                userId = userIdBody,
                image = imagePart
            ).collect {
                emit(it)
            }
        }

    fun repComment(replyData: ReplyData, imagePart: MultipartBody.Part?) =
        flow<BaseResponse<ReplyResponse>> {
            mainRepository.reply(
                user_id = replyData.user_id.toString().toRequestBody("text/plain".toMediaType()),
                content = replyData.content.toRequestBody("text/plain".toMediaType()),
                comment_id = replyData.comment_id.toString()
                    .toRequestBody("text/plain".toMediaType()),
                reply_user_id = replyData.reply_user_id.toString()
                    .toRequestBody("text/plain".toMediaType()),
                image = imagePart
            ).collect {
                emit(it)
            }
        }

    fun getReply(video_id: String?, comment_id: Int, page: Int) = flow<BaseResponse<List<Reply>>> {
        mainRepository.getReply(
            video_id = video_id, comment_id = comment_id, page = page
        ).collect {
            emit(it)
        }
    }

    val isLoggedIn: Flow<Boolean> = databaseManager.isLogin

}