package com.example.movieapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat

class Notification(context: Context) {
    private val channelId = "movie_app"
    var notificationId: Int = 1234
        internal set
    private val builder: NotificationCompat.Builder
    private var notificationManager: NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Download/Processing",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }


    fun getBuilder() = builder

    fun updateProgress(progress: Int, movieName: String, current: Int) {
        builder
            .setContentTitle("Tải xuống $movieName - Tập ${current + 1}")
            .setContentText("$progress %")
            .setProgress(100, progress, false)
            .setSmallIcon(android.R.drawable.stat_sys_download)
        notificationManager.notify(notificationId, builder.build())
    }

    fun complete(movieName: String, current: Int) {
        builder.setContentTitle("Tải xuống $movieName - Tập ${current + 1}")
            .setContentText("Đã tải xuống video")
            .setProgress(0, 0, false)
            .setOngoing(true)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
        notificationManager.notify(notificationId, builder.build())
    }

}