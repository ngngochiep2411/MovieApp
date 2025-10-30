package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem

data class StartExpandReducer(
    val folding: CommentItem.Folding,
) : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        map {
            if (it is CommentItem.Folding && it == folding) {
                it.copy(
                    state = CommentItem.Folding.State.LOADING
                )
//                it.state= CommentItem.Folding.State.LOADING
            } else {
                it
            }
        }
    }

}