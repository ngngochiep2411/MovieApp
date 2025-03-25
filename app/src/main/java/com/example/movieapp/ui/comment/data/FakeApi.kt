package com.example.movieapp.ui.comment.data

import kotlinx.coroutines.delay

object FakeApi {
    private var id = 0

    /**
     * 每页返回pageSize个一级评论，每个一级评论返回最多两个热门二级评论
     */
    suspend fun getComments(page: Int, pageSize: Int = 5): Result<List<ICommentEntity>> {
        delay(2000)
        val list = (0 until pageSize).map {
            val id = id++
            CommentLevel1(
                id = id,
                content = "Tôi là bình luận cấp độ một${id}",
                userId = 1,
                userName = "- Bình luận viên cấp một",
                level2Count = 20
            )
        }.map {
            listOf(
                it,
                CommentLevel2(
                    id = id++,
                    content = "Tôi là bình luận cấp độ 2$id",
                    userId = 2,
                    userName = "二级评论员",
                    parentId = it.id,
                    hot = true,
                ), CommentLevel2(
                    id = id++,
                    content = "Tôi là bình luận cấp độ 2$id",
                    userId = 2,
                    userName = "二级评论员",
                    parentId = it.id,
                    hot = true,
                )
            )
        }.flatten()
        return Result.success(list)
    }

    suspend fun getLevel2Comments(
        parentId: Int,
        page: Int,
        pageSize: Int = 3
    ): Result<List<ICommentEntity>> {
        delay(500)
        if (page > 5) return Result.success(emptyList())
        val list = (0 until pageSize).map {
            CommentLevel2(
                id = id++,
                content = "Tôi là bình luận cấp độ 2$id",
                userId = 2,
                userName = "二级评论员",
                parentId = parentId,
            )
        }
        return Result.success(list)
    }

    suspend fun addComment(
        id: Int,
        content: String,
        replyUserName: String,
    ): CommentLevel2 {
        delay(400)
        FakeApi.id++
        return CommentLevel2(
            id = FakeApi.id++,
            content,
            userId = 3,
            userName = "哈哈哈哈",
            parentId = id,
            replyUserName = replyUserName
        )
    }
}