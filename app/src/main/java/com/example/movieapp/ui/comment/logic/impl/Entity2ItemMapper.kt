package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.ui.comment.data.CommentLevel1
import com.example.movieapp.ui.comment.data.CommentLevel2
import com.example.movieapp.ui.comment.data.ICommentEntity
import com.example.movieapp.ui.comment.logic.Mapper
import com.example.movieapp.ui.comment.ui.CommentItem
import com.example.movieapp.ui.comment.ui.makeHot

class Entity2ItemMapper : Mapper<ICommentEntity, CommentItem> {
    override fun invoke(entity: ICommentEntity): CommentItem {
        return when (entity) {
            is CommentLevel1 -> {
                CommentItem.Level1(
                    id = entity.id,
                    content = entity.content,
                    userId = entity.userId,
                    userName = entity.userName,
                    level2Count = entity.level2Count,
                )
            }

            is CommentLevel2 -> {
                CommentItem.Level2(
                    id = entity.id,
                    content = if (entity.hot) entity.content.makeHot() else entity.content,
                    userId = entity.userId,
                    userName = entity.userName,
                    parentId = entity.parentId,
                    userReply = entity.replyUserName
                )
            }

            else -> {
                throw IllegalArgumentException("not implemented entity: $entity")
            }
        }
    }
}