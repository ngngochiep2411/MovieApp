package com.example.movieapp.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.movieapp.R

class ToolbarDetailUser(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.layout_toolbar_detail_user, this, true)

//        disableSave()

    }


    private fun disableSave() {
        findViewById<TextView>(R.id.save).alpha = 0.5f
        findViewById<TextView>(R.id.save).isEnabled = false
    }

    fun enabledSave(){
        findViewById<TextView>(R.id.save).alpha = 1f
        findViewById<TextView>(R.id.save).isEnabled = true
    }
}