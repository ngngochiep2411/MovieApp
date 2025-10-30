package com.example.movieapp.widgets

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import com.example.movieapp.R
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class CustomGSYPlayer(context: Context, attrs: AttributeSet? = null) :
    StandardGSYVideoPlayer(context, attrs) {

    private lateinit var imgFullScreen: ImageView

    override fun init(context: Context?) {
        super.init(context)
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_custom_controller
    }

    override fun setUp(url: String?, cacheWithPlay: Boolean, title: String?): Boolean {
        return super.setUp(url, cacheWithPlay, title)
    }


}