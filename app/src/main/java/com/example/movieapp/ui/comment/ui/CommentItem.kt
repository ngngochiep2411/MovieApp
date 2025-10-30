package com.example.movieapp.ui.comment.ui

import com.example.movieapp.ui.comment.ui.CommentItem.Loading.State

sealed interface CommentItem {
    val id: Int
    val content: CharSequence
    val userId: Int?
    val userName: CharSequence

    data class Empty(
        val page: Int = 0,
    ) : CommentItem {
        override val id: Int = 0
        override val content: CharSequence = ""
        override val userId: Int = 0
        override val userName: CharSequence = ""
    }

    data class FirstLoading(
        val page: Int = 0, val state: State = State.LOADING
    ) : CommentItem {
        override val id: Int = 0
        override val content: CharSequence = ""
        override val userId: Int = 0
        override val userName: CharSequence = ""

    }

    data class Loading(
        val page: Int = 0, val state: State = State.LOADING
    ) : CommentItem {
        override val id: Int = 0
        override val content: CharSequence
            get() = when (state) {
                State.LOADED_ALL -> "Tải tất cả"
                else -> "Đang tải..."
            }
        override val userId: Int = 0
        override val userName: CharSequence = ""

        enum class State {
            IDLE, LOADING, LOADED_ALL
        }
    }

    data class Level1(
        override val id: Int,
        override val content: CharSequence,
        override val userId: Int?,
        override val userName: CharSequence,
        val level2Count: Int,
        var like: Boolean = false,
        var unLike: Boolean = false,
        var likeCount: Int = 0,
        val avatar_url: String? = "",
        val image: String? = "",
        val time: String
    ) : CommentItem

    data class Level2(
        override val id: Int,
        override val content: CharSequence,
        override val userId: Int,
        val userReply: String? = null,
        override val userName: CharSequence,
        val parentId: Int,
        var like: Boolean = false,
        var unLike: Boolean = false,
        var likeCount: Int = 0,
        val time: String,
        val avatar_url: String? = "",
        val image: String? = "",
    ) : CommentItem

    data class Folding(
        val parentId: Int,
        val page: Int = 1,
        val pageSize: Int = 3,
        val state: State = State.IDLE,
        val count: Int,
        var total: Int,
        val current: Int,
        var nextPage: Boolean = false,
        val replies: MutableList<CommentItem> = mutableListOf()
    ) : CommentItem {


        override val id: Int
            get() = parentId
        override val content: CharSequence = "Xem thêm $count câu trả lời khác"
        override val userId: Int = 0
        override val userName: CharSequence = ""

        enum class State {
            IDLE, LOADING, LOADED_ALL, COLLAPSE
        }

        val text: String = "Xem thêm ${total - current} câu trả lời khác"

    }
}