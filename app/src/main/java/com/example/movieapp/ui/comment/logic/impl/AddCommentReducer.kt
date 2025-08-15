package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.model.CommentResponse
import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem
import kotlin.random.Random

class AddCommentReducer(comment: CommentResponse) : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        this.toMutableList().apply {
            removeAll { it is CommentItem.Empty }
            add(
                0, CommentItem.Level1(
                    id = comment.id,
                    content = comment.content,
                    userId = comment.user?.id,
                    userName = comment.user?.name.toString(),
                    level2Count = Random.nextInt(10000),
                    avatar_url = comment.user?.avatar_url,
                    time = comment.createdAt,
                    image = comment.image
                )
            )
        }.toList()
    }

}