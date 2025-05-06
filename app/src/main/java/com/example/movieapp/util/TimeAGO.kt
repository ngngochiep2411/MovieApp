package com.example.movieapp.util

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.type.DateTime
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object TimeAGO {


    fun convertString(dateTime: String): String {
        val now = Date()
        val cleanedString = dateTime.replace(".000000Z", "+0000")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = sdf.parse(cleanedString)
        val seconds = (now.time - date.time) / 1000

        return when {
            seconds < 60 -> "${seconds} giây trước"
            seconds < 60 * 60 -> "${seconds / 60} phút trước"
            seconds < 60 * 60 * 24 -> "${seconds / 3600} giờ trước"
            seconds < 60 * 60 * 24 * 30 -> "${seconds / 86400} ngày trước"
            seconds < 60 * 60 * 24 * 30 * 12 -> "${seconds / 2592000} tháng trước"
            else -> "${seconds / 60 * 60 * 24 * 30 * 12} năm trước"
        }

    }

}