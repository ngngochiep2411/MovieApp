package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem

class StartLoadLv1Reducer : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        map {
            if (it is CommentItem.Loading) {
                it.copy(
                    state = CommentItem.Loading.State.LOADING
                )
            } else {
                it
            }
        }
    }
}