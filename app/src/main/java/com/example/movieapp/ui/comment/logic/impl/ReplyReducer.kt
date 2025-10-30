package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.model.ReplyResponse
import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem

class ReplyReducer(
    private val commentItem: CommentItem,
    response: ReplyResponse,
) : Reducer {

    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {

        val insertIndex = indexOf(commentItem) + 1

        val parentId = when (commentItem) {
            is CommentItem.Level1 -> commentItem.id
            is CommentItem.Level2 -> commentItem.parentId
            else -> null
        }

        val folding = this.find {
            it is CommentItem.Folding && it.parentId == parentId
        } as? CommentItem.Folding
        folding?.total = folding.total + 1

        val addComment = CommentItem.Level2(
            id = response.id,
            time = response.created_at,
            userId = response.user.id,
            userReply = if (response.user.id == response.reply_user?.id) null else response.reply_user?.name,
            content = response.content,
            unLike = false,
            like = false,
            parentId = response.comment_id,
            userName = response.user.name,
            likeCount = 22,
            avatar_url = response.user.avatar_url,
            image = response.image,
        )
        folding?.replies?.add(addComment)
        toMutableList().apply {
            add(insertIndex, addComment)
        }
    }
}

