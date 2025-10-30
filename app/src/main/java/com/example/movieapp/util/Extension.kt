package com.example.movieapp.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.movieapp.R

object Extension {

    fun TextView.setRoundedStartIconUrl(
        url: String?,
        sizePx: Int,
        userName: String,
        content: String,
    ) {
        val fullText = "$userName  $content"
        val spannable = SpannableString("  $fullText")

        val nameStart = 2
        val nameEnd = nameStart + userName.length
        val contentStart = nameEnd
        val contentEnd = contentStart + content.length

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.gray82)),
            nameStart,
            nameEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.WHITE),
            contentStart,
            contentEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        this.text = spannable

        val circleProgressbar = CircularProgressDrawable(context).apply {
            setColorSchemeColors(ContextCompat.getColor(context, R.color.primaryColor))
            centerRadius = 30f
            strokeWidth = 5f
            start()
        }

        val toLoad: Any = if (url.isNullOrEmpty()) R.drawable.avatar_anonymous else url

        Glide.with(context)
            .asBitmap()
            .load(toLoad)
            .placeholder(circleProgressbar)
            .error(R.drawable.avatar_anonymous)
            .override(sizePx, sizePx)
            .into(object : CustomTarget<Bitmap>(sizePx, sizePx) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val rounded = createBitmap(sizePx, sizePx)
                    val canvas = Canvas(rounded)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    val rect = RectF(0f, 0f, sizePx.toFloat(), sizePx.toFloat())

                    canvas.drawRoundRect(rect, sizePx / 2f, sizePx / 2f, paint)
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    canvas.drawBitmap(resource, null, rect, paint)

                    val roundedDrawable = rounded.toDrawable(resources).apply {
                        setBounds(0, 0, sizePx, sizePx)
                    }

                    val imageSpan = ImageSpan(roundedDrawable, ImageSpan.ALIGN_BOTTOM)
                    spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    this@setRoundedStartIconUrl.text = spannable
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholder?.setBounds(0, 0, sizePx, sizePx)
                    placeholder?.let {
                        val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BOTTOM)
                        spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        this@setRoundedStartIconUrl.text = spannable
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    val err = errorDrawable ?: ContextCompat.getDrawable(
                        context,
                        R.drawable.avatar_anonymous
                    )
                    err?.setBounds(0, 0, sizePx, sizePx)
                    err?.let {
                        val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BOTTOM)
                        spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        this@setRoundedStartIconUrl.text = spannable
                    }
                }
            })
    }

}
