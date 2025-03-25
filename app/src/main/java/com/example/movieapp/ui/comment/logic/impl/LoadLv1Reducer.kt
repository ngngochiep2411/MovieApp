package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.model.Comment
import com.example.movieapp.ui.comment.data.FakeApi
import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem

class LoadLv1Reducer(comments: List<Comment>, userId: Int?) : Reducer {
    private val mapper by lazy { Entity2ItemMapper() }
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {

        val loading = get(size - 1) as CommentItem.Loading

//        val loaded =
//            FakeApi.getComments(loading.page + 1, 5).getOrNull()?.map(mapper::invoke) ?: emptyList()
//
//        val grouped = loaded.groupBy {
//            (it as? CommentItem.Level1)?.id ?: (it as? CommentItem.Level2)?.parentId
//            ?: throw IllegalArgumentException("invalid comment item")
//        }.flatMap {
//            it.value + CommentItem.Folding(
//                parentId = it.key,
//            )
//        }
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
                    level2Count = 2
                )
            )

            if (comment.replys.isNotEmpty()) {
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
                            time = "",
                            parentId = comment.id
                        )
                    )

                }
            }
        }



        toMutableList().apply {
            removeAt(size - 1)
            this += newList
//            this += loading.copy(
//                state = CommentItem.Loading.State.IDLE, page = loading.page + 1
//            )
        }.toList()
    }
}