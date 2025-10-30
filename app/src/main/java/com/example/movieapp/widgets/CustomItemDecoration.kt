package com.example.movieapp.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomItemDecoration(private val spanCount: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val totalItemCount = state.itemCount

        val isNotLastRow = (position / spanCount) != (totalItemCount / spanCount)
        val isNotFirst2Items = position >= 3

        if (isNotFirst2Items && isNotLastRow) {
            outRect.bottom = 15
        } else {
            outRect.bottom = 0
        }
        if (isNotFirst2Items) {
            val column = position % spanCount
            if (column == 0 || column == 1) {
                outRect.right = 15
            }
        }

    }
}

