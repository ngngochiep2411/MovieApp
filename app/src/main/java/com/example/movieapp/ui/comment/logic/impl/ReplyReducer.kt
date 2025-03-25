package com.example.movieapp.ui.comment.logic.impl

import android.content.Context
import com.example.movieapp.ui.comment.data.FakeApi
import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem
import com.example.movieapp.widgets.ReplyDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReplyReducer(
    private val commentItem: CommentItem,
    private val context: Context,
    addComment: CommentItem
) : Reducer {
    private val mapper: Entity2ItemMapper by lazy { Entity2ItemMapper() }
    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {

//        val content = withContext(Dispatchers.Main) {
//            suspendCoroutine { continuation ->
//                ReplyDialog(context, commentItem.userName) {
//                    continuation.resume(it)
//                }.show()
//            }
//        }
//        val parentId = (commentItem as? CommentItem.Level1)?.id
//            ?: (commentItem as? CommentItem.Level2)?.parentId ?: 0
//        val replyItem =
//            mapper.invoke(FakeApi.addComment(parentId, content, commentItem.userName.toString()))
        val insertIndex = indexOf(commentItem) + 1
        toMutableList().apply {
            add(insertIndex, addComment)
        }
    }
}

