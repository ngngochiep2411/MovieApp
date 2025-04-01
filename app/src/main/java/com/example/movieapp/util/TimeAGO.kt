package com.example.movieapp.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.Instant

object TimeAGO {

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertString(dateTime: String): String {
        if (dateTime.isNotEmpty()) {
            val text = Instant.parse(dateTime)
            val now = Instant.now()
            val duration = Duration.between(text, now)

            val seconds = duration.seconds
            val minutes = duration.toMinutes()
            val hours = duration.toHours()
            val days = duration.toDays()
            val months = days / 30
            val years = days / 365


            val result = when {
                seconds < 60 -> "$seconds giây trước"
                minutes < 60 -> "$minutes phút trước"
                hours < 24 -> "$hours giờ trước"
                days < 30 -> "$days ngày trước"
                months == 1L -> "1 tháng trước"
                months < 12 -> "$months tháng trước"
                years == 1L -> "1 năm trước"
                years > 1 -> "$years năm trước"
                else -> "Hơn một năm trước"
            }
            return result
        }
        return ""

    }

}