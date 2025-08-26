package com.example.movieapp.widgets

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView


class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    private var originalText: CharSequence? = null
    private var isExpanded = false
    private val maxCollapsedLines = 6
    private val expandText = "...Xem thêm"
    private val collapseText = "Thu gọn"

    fun setTextExpandable(text: CharSequence) {
        originalText = text
        setExpanded(false)
    }

    private fun setExpanded(expand: Boolean) {
        isExpanded = expand
        if (expand) {
            // Hiển thị toàn bộ + nút Thu gọn
            val spannable = SpannableStringBuilder(originalText).append(collapseText)
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    setExpanded(false)
                }
            }, spannable.length - collapseText.length, spannable.length, 0)

            text = spannable
        } else {
            // Thu gọn 6 dòng
            maxLines = maxCollapsedLines

            post {
                if (layout != null ) {
                    val endIndex = layout.getLineEnd(maxCollapsedLines - 1)
                    val truncated =
                        originalText?.subSequence(0, endIndex - expandText.length - 3) ?: ""
                    val spannable = SpannableStringBuilder("$truncated...").append(expandText)

                    spannable.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            setExpanded(true)
                        }
                    }, spannable.length - expandText.length, spannable.length, 0)

                    text = spannable
                } else {
                    text = originalText
                }
            }
        }
        movementMethod = LinkMovementMethod.getInstance()
    }
}