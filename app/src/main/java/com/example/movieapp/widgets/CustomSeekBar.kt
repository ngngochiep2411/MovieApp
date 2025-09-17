package com.example.movieapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar

class CustomSeekBar(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatSeekBar(context, attrs) {


    private var isDragging = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Ghi nhận có chạm, nhưng KHÔNG cho super xử lý
                // => progress sẽ không bị nhảy tới vị trí chạm
                isDragging = true
                parent.requestDisallowInterceptTouchEvent(true)
                // Trả true để tiếp tục nhận MOVE/UP
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    // Gọi super để cập nhật progress chỉ khi kéo
                    super.onTouchEvent(event)
                }
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    super.onTouchEvent(event)   // cập nhật lần cuối
                    isDragging = false
                }
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return false
    }
}