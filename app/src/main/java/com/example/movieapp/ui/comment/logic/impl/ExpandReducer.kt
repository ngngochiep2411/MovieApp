package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.model.PaginationV2
import com.example.movieapp.model.Reply
import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem
import com.example.movieapp.ui.comment.ui.CommentItem.Folding.State

data class ExpandReducer(
    val folding: CommentItem.Folding,
    val replys: List<Reply> = emptyList(),
    val pagination: PaginationV2? = null
) : Reducer {
    private val mapper by lazy { Entity2ItemMapper() }
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val foldingIndex = indexOf(folding)
//        val loaded =
//            FakeApi.getLevel2Comments(folding.parentId, folding.page, folding.pageSize).getOrNull()
//                ?.map(mapper::invoke) ?: emptyList()

        pagination?.nextPage =
            (getCurrentReply(folding.parentId, this) + replys.size < (pagination?.total ?: 0))

        val loaded = mutableListOf<CommentItem>()
        if (folding.state == State.COLLAPSE) {
            loaded.addAll(folding.replies)
        } else {
            for (reply in replys) {
                loaded.add(
                    CommentItem.Level2(
                        id = reply.id,
                        content = reply.content,
                        userId = reply.userId,
                        unLike = false,
                        like = false,
                        userName = reply.getUserName(),
                        likeCount = 22,
                        userReply = if (reply.user?.id == reply.user?.id || reply.user?.id == reply.reply_user?.id) null else reply.reply_user?.name,
                        time = "",
                        parentId = reply.commentId,
                        avatar_url = reply.user?.avatarUrl
                    )
                )
            }
        }

        toMutableList().apply {
            addAll(foldingIndex, loaded)
        }.map {
            if (it is CommentItem.Folding && it == folding) {
                val state =
                    if (pagination?.nextPage != true
                    ) CommentItem.Folding.State.LOADED_ALL else CommentItem.Folding.State.IDLE
                val count =
                    (pagination?.total ?: 0) - getCurrentReply(folding.parentId, this) - replys.size
                it.copy(
                    page = it.page + 1,
                    state = state,
                    count = count,
                    replies = it.replies + replys.convertReply(),
                    current = getCurrentReply(folding.parentId, this) + replys.size
                )
            } else {
                it
            }
        }
    }

    private fun getCurrentReply(parentId: Int, list: List<CommentItem>): Int {
        return list.count { it is CommentItem.Level2 && it.parentId == parentId }
    }

}

