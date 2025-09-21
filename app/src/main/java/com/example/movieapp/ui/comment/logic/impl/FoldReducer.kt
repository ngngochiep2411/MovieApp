package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem

data class FoldReducer(val folding: CommentItem.Folding) : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val parentIndex = indexOfFirst {
            it.id == folding.parentId
        }
        val foldingIndex = indexOf(folding)
        (this - subList(parentIndex + 1, foldingIndex).toSet()).map {
            if (it is CommentItem.Folding && it == folding) {
                it.copy(state = CommentItem.Folding.State.COLLAPSE, current = 0)
            } else {
                it
            }
        }
    }
}

