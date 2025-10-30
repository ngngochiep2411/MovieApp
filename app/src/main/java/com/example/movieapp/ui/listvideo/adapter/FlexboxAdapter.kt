package com.example.movieapp.ui.listvideo.adapter


import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.movieapp.R
import com.google.android.flexbox.FlexboxLayout


class FlexboxAdapter(
    val context: Context,
    var list: List<String> = emptyList<String>(),
    val flexboxLayout: FlexboxLayout
) {

    var index = 0

    fun updateIndex(index: Int) {
        this.index = index
        notifyDataSetChanged()
    }


    fun setAdapter(list: List<String>) {
        this.list = list
        for (i in list.indices) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.layout_flexbox_item, null, false)
            val layoutParam = view.layoutParams as LinearLayout.LayoutParams
            layoutParam.marginEnd = 4
            layoutParam.bottomMargin = 4
            view.layoutParams = layoutParam
            val textView: TextView = view.findViewById<TextView>(R.id.text)
            textView.text = list[i]
            if (index == i) {
                textView.setTextColor(context.getColor(R.color.primaryColor))
            }
            view.setOnClickListener {
                this.index = i
                notifyDataSetChanged()
            }
            flexboxLayout.addView(view)
        }
    }

    private fun notifyDataSetChanged() {
        val childCount = flexboxLayout.childCount
        for (i in 0 until childCount) {
            val textView = (flexboxLayout.getChildAt(i) as LinearLayout).getChildAt(0) as TextView
            if (index == i) {
                textView.setTextColor(context.getColor(R.color.primaryColor))
            } else {
                textView.setTextColor(context.getColor(R.color.white))
            }
        }
    }
}