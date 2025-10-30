package com.example.movieapp.widgets

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StartMarginItemDecoration(
    private val marginStartPx: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: android.graphics.Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position > 0) {
            outRect.left = marginStartPx
        }
    }
}