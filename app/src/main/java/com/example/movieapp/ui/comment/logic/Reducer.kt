package com.example.movieapp.ui.comment.logic

import com.example.movieapp.ui.comment.ui.CommentItem

interface Reducer {
    val reduce: suspend List<CommentItem>.() -> List<CommentItem>
}