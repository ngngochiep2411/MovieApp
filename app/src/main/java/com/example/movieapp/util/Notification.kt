package com.example.movieapp.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.movieapp.service.DownloadService

class Notification(val context: Context) {
    private val channelId = "movie_app"
    var notificationId: Int = 1234
        internal set
    private val builder: NotificationCompat.Builder
    private var notificationManager: NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Download/Processing", NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download).setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }


    fun getBuilder() = builder

    @SuppressLint("RestrictedApi")
    fun updateProgress(progress: Double, movieName: String, position: Int, slug: String?) {
        val cancelIntent = Intent(context, DownloadService::class.java).apply {
            action = DownloadService.ACTION_CANCEL
            putExtra(DownloadService.EXTRA_POSITION, position)
            putExtra(DownloadService.EXTRA_SLUG, slug)
        }
        val cancelPendingIntent = PendingIntent.getService(
            context,
            position,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        builder.mActions.clear()

        builder.setContentTitle("Tải xuống $movieName - Tập ${position + 1}")
            .setContentText("")
            .setSubText("${progress.toInt()} %").setProgress(100, progress.toInt(), false)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Hủy tải xuống", cancelPendingIntent)


        notificationManager.notify(notificationId, builder.build())
    }

    @SuppressLint("RestrictedApi")
    fun complete(movieName: String, position: Int?, success: Boolean) {
        builder.mActions.clear()
        builder.setContentTitle("Tải xuống $movieName - Tập ${position?.plus(1)}").setSubText("")
            .setContentText(if (!success) "Có lỗi xảy ra" else "Đã tải xuống video")
            .setProgress(0, 0, false)
            .setOngoing(true)
        notificationManager.notify(notificationId, builder.build())
    }

    @SuppressLint("RestrictedApi")
    fun onStart(progress: Double, movieName: String, position: Int, slug: String?) {
        builder.mActions.clear()

        builder.setContentTitle("Tải xuống $movieName - Tập ${position + 1}")
            .setContentText("")
            .setSubText("${progress.toInt()} %").setProgress(100, progress.toInt(), false)
            .setOngoing(true)


        notificationManager.notify(notificationId, builder.build())
    }
}
