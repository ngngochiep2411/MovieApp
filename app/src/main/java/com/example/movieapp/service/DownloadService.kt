package com.example.movieapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import androidx.core.content.IntentCompat
import com.example.movieapp.model.ServerData
import com.example.movieapp.util.VideoDownloader

class DownloadService : Service() {

    private val binder = LocalBinder()
    private lateinit var videoDownloader: VideoDownloader

    inner class LocalBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }

    override fun onCreate() {
        super.onCreate()
        videoDownloader = VideoDownloader(this)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val list: ArrayList<ServerData>? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableArrayListExtra("urls", ServerData::class.java)
            } else {
                @Suppress("DEPRECATION") intent?.getParcelableArrayListExtra("urls")
            }
        val slug = intent?.getStringExtra("slug")
        if (list != null && slug != null) {
            videoDownloader.downLoadVideo(
                list = list, slug = slug,
                onDownloadStart = { index, fileName ->
                    showNotification(id = 1000 + index, "Bắt đầu tải$fileName")
                },
                onProgress = { index, fileName, progress ->

                },
                onDownloadComplete = { index, fileName, success ->


                }
            )
        }
        return START_NOT_STICKY
    }

    private fun showNotification(id: Int, content: String) {
        val channelId = "download_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Download Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Trạng thái tải video")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }
}
