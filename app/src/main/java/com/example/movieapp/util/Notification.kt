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

    fun updateProgress(progress: Double, movieName: String, position: Int) {
        builder
            .setContentTitle("Tải xuống $movieName - Tập ${position + 1}")
            .setContentText("${progress.toInt()} %")
            .setProgress(100, progress.toInt(), false)
        notificationManager.notify(notificationId, builder.build())
    }

    fun complete(movieName: String, position: Int?, success: Boolean) {
        if (success) {
            builder.setContentTitle("Tải xuống $movieName - Tập ${position?.plus(1)}")
                .setContentText("Đã tải xuống video")
                .setProgress(0, 0, false)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
            notificationManager.notify(notificationId, builder.build())
        } else {
            builder.setContentTitle("Tải xuống $movieName - Tập ${position?.plus(1)}")
                .setContentText("Có lỗi xảy ra")
                .setProgress(0, 0, false)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
            notificationManager.notify(notificationId, builder.build())
        }

    }

}