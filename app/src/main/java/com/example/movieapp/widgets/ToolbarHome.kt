package com.example.movieapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.movieapp.R

class ToolbarHome(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var imgSearch: ImageView

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.layout_toolbar_home, this, true)

        imgSearch = findViewById(R.id.imgSearch)
        imgSearch.setOnClickListener {
            onItemClickListener?.onSearchClick()
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onSearchClick()
    }


}