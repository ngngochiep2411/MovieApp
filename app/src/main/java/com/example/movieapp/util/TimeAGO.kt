package com.example.movieapp.util

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.type.DateTime
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object TimeAGO {


    fun getTimeAgo(isoTime: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val inputTime = ZonedDateTime.parse(isoTime, formatter)
        val now = ZonedDateTime.now(ZoneOffset.UTC)

        val seconds = ChronoUnit.SECONDS.between(inputTime, now)
        val minutes = ChronoUnit.MINUTES.between(inputTime, now)
        val hours = ChronoUnit.HOURS.between(inputTime, now)
        val days = ChronoUnit.DAYS.between(inputTime, now)
        val months = ChronoUnit.MONTHS.between(inputTime, now)
        val years = ChronoUnit.YEARS.between(inputTime, now)

        return when {
            seconds < 60 -> "$seconds giây trước"
            minutes < 60 -> "$minutes phút trước"
            hours < 24 -> "$hours giờ trước"
            days < 30 -> "$days ngày trước"
            months < 12 -> "$months tháng trước"
            else -> "$years năm trước"
        }
    }

}