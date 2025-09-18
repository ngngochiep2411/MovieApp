package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem

class ReplyReducer(
    private val commentItem: CommentItem,
    addComment: CommentItem,
) : Reducer {

    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val insertIndex = indexOf(commentItem) + 1
        toMutableList().apply {
            add(insertIndex, addComment)
        }
    }
}

