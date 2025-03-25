package com.example.movieapp.util

import android.content.Intent
import android.graphics.Rect
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.widget.NestedScrollView

object Extension {

    inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
        SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key)
    }

    inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
        SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
    }
}

fun View.getHeightOnGlobalLayout(): Int {
    var height = 0
    this.viewTreeObserver.addOnGlobalLayoutListener {
        height = this.height
    }
    return height
}

fun isViewVisibleInScrollView(scrollView: NestedScrollView, targetView: View): Boolean {
    val scrollBounds = Rect()

    scrollView.getDrawingRect(scrollBounds)

    val location = IntArray(2)
    targetView.getLocationOnScreen(location)
    val targetRect = Rect(location[0], location[1], location[0] + targetView.width, location[1] + targetView.height)


    return Rect.intersects(scrollBounds, targetRect)
}