package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem

data class ExpandReplyLoadedReducer(
    val folding: CommentItem.Folding
) : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val foldingIndex = indexOf(folding)
        this.toMutableList().apply {
            addAll(foldingIndex, folding.replies)
        }.map {
            if (it is CommentItem.Folding && it == folding) {
                it.copy(
                    state = if (folding.nextPage) CommentItem.Folding.State.IDLE else CommentItem.Folding.State.LOADED_ALL
                )
            } else {
                it
            }
        }
    }
}
