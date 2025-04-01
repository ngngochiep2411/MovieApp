package com.example.movieapp.ui.comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.BaseResponse
import com.example.movieapp.model.Comment
import com.example.movieapp.model.CommentData
import com.example.movieapp.model.CommentResponse
import com.example.movieapp.model.GetReply
import com.example.movieapp.model.Reply
import com.example.movieapp.model.ReplyData
import com.example.movieapp.model.ReplyResponse
import com.example.movieapp.model.User
import com.example.movieapp.model.UserDetail
import com.example.movieapp.repository.MainRepository
import com.example.movieapp.util.DataStoreManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comment>?>(null)
    val comments: StateFlow<List<Comment>?> = _comments

    private val _moreComments = MutableStateFlow<List<Comment>?>(null)
    val moreComments: StateFlow<List<Comment>?> = _moreComments

    private var currentPage = 1
    var nextPage = false

    var userDetail: User? = null

    init {
        viewModelScope.launch {
            dataStoreManager.userDetail.collect {
                userDetail = it
            }
        }

    }


    fun getComment(videoName: String?) {
        viewModelScope.launch {
            mainRepository.getComment(videoName, currentPage++).collect {
                currentPage = it.pagination.currentPage
                nextPage = it.pagination.nextPage
                _comments.value = it.data
            }
        }

    }

    fun getMoreComment(videoName: String?) {
        viewModelScope.launch {
            mainRepository.getComment(videoName, currentPage++).collect {
                currentPage = it.pagination.currentPage
                nextPage = it.pagination.nextPage
                _moreComments.value = it.data
            }
        }

    }

    fun comment(comment: CommentData) = flow<BaseResponse<CommentResponse>> {
        mainRepository.comment(comment).collect {
            Log.d("testing", Gson().toJson(it))
            emit(it)
        }
    }

    fun repComment(replyData: ReplyData) = flow<BaseResponse<ReplyResponse>> {
        mainRepository.reply(replyData).collect {
            Log.d("testing", Gson().toJson(it))
            emit(it)
        }
    }

    fun getReply(video_id: String?, comment_id: Int, page: Int) = flow<BaseResponse<List<Reply>>> {
        mainRepository.getReply(
            video_id = video_id,
            comment_id = comment_id,
            page = page
        ).collect {
            Log.d("testing", Gson().toJson(it))
            emit(it)
        }
    }

    val isLoggedIn: Flow<Boolean> = dataStoreManager.isLogin

}