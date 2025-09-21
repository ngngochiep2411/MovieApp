package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.model.Comment
import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem

class LoadLv1Reducer(comments: List<Comment>, userId: Int?, val nextPage: Boolean) : Reducer {
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val newList = mutableListOf<CommentItem>()
        for (comment in comments) {
            newList.add(
                CommentItem.Level1(
                    id = comment.id,
                    content = comment.content,
                    userId = comment.userId,
                    unLike = false,
                    like = false,
                    userName = comment.getUserName(),
                    likeCount = 22,
                    level2Count = 2,
                    time = comment.createdAt
                )
            )

            if (!comment.replys.isNullOrEmpty()) {
                for (reply in comment.replys) {
                    newList.add(
                        CommentItem.Level2(
                            id = reply.id,
                            content = reply.content,
                            userId = reply.userId,
                            unLike = false,
                            like = false,
                            userName = reply.getUserName(),
                            likeCount = 22,
                            userReply = if (userId == reply.reply_user?.id) null else reply.reply_user?.name,
                            time = reply.createdAt,
                            parentId = comment.id
                        )
                    )

                }
            }
        }

        if (nextPage) {
            newList.add(
                CommentItem.Loading()
            )
        }


        toMutableList().apply {
            removeAt(size - 1)
            this += newList
        }.toList()
    }
}