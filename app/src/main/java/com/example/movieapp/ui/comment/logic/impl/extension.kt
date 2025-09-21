package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.model.Comment
import com.example.movieapp.model.Reply
import com.example.movieapp.ui.comment.ui.CommentItem
import com.example.movieapp.ui.comment.ui.CommentItem.Folding.State


fun List<Comment>.convertComment(
    nextPage: Boolean
): List<CommentItem> {
    val newList = mutableListOf<CommentItem>()

    for (comment in this) {
        newList.add(
            CommentItem.Level1(
                id = comment.id,
                content = comment.content,
                userId = comment.user?.id,
                unLike = false,
                like = false,
                userName = comment.getUserName(),
                likeCount = 22,
                level2Count = 2,
                avatar_url = comment.user?.avatar_url,
                time = comment.createdAt,
                image = comment.image
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
                        userReply = if (comment.user?.id == reply.user?.id || reply.user?.id == reply.reply_user?.id) null else reply.reply_user?.name,
                        time = comment.createdAt,
                        parentId = comment.id,
                        avatar_url = reply.user?.avatar_url,
                        image = reply.image
                    )
                )

            }
        }

        val folding = CommentItem.Folding(
            parentId = comment.id,
            page = comment.paginationReply.currentPage,
            state = State.IDLE,
            pageSize = 10,
            count = comment.paginationReply.total - comment.paginationReply.count,
            total = comment.paginationReply.total,
            current = comment.paginationReply.count,
            nextPage = false
        )
        if (comment.paginationReply.nextPage) {
            folding.nextPage = true
            newList.add(folding)
        }

    }
    if (nextPage) {
        newList.add(CommentItem.Loading())
    }
    return newList
}

fun List<Reply>.convertReply(): MutableList<CommentItem.Level2> {
    val list = mutableListOf<CommentItem.Level2>()
    this.forEach { reply ->
        list.add(
            CommentItem.Level2(
                id = reply.id,
                content = reply.content,
                userId = reply.userId,
                unLike = false,
                like = false,
                userName = reply.getUserName(),
                likeCount = 22,
                userReply = if (reply.user?.id == reply.user?.id || reply.user?.id == reply.reply_user?.id) null else reply.reply_user?.name,
                time = reply.createdAt,
                parentId = reply.commentId,
                avatar_url = reply.user?.avatar_url,
                image = reply.image
            )
        )

    }
    return list
}