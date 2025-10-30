package com.example.movieapp.ui.comment.logic.impl

import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.ui.CommentItem
import kotlinx.coroutines.delay

class LoadDataReducer : Reducer {

    override val reduce: suspend List<CommentItem>.() -> List<CommentItem> = {
        val loading = get(size - 1) as CommentItem.FirstLoading
        delay(3000L)
        val loaded = mutableListOf<CommentItem>()
//        val loaded =
//            FakeApi.getComments(loading.page + 1, 5).getOrNull()?.map(mapper::invoke)
//                ?: emptyList()
//
//        val grouped = loaded.groupBy {
//            (it as? CommentItem.Level1)?.id ?: (it as? CommentItem.Level2)?.parentId
//            ?: throw IllegalArgumentException("invalid comment item")
//        }.flatMap {
//            it.value + CommentItem.Folding(
//                parentId = it.key,
//            )
//        }

        toMutableList().apply {
            removeAt(size - 1)
            if (loaded.size == 0) add(CommentItem.Empty())
        }.toList()
    }

}