package com.example.movieapp.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.movieapp.model.ServerData
import com.example.movieapp.ui.listvideo.adapter.DownloadState
import com.example.movieapp.util.Notification
import com.example.movieapp.util.VideoDownloader

class DownloadService : Service() {

    private val binder = LocalBinder()
    private lateinit var videoDownloader: VideoDownloader
    private lateinit var notificationHelper: Notification

    inner class LocalBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }

    fun isDownloading(): Boolean {
        return videoDownloader.isDownloading
    }

    override fun onCreate() {
        videoDownloader = VideoDownloader(this)
        notificationHelper = Notification(this)
        startForeground(
            notificationHelper.notificationId,
            notificationHelper.getBuilder()
                .build()
        )
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url")
        val slug = intent?.getStringExtra("slug")
        val movieName = intent?.getStringExtra("movieName")
        val position = intent?.getIntExtra("position", -1)
        if (url != null && slug != null && movieName != null && position != -1) {
            videoDownloader.download(
                url = url,
                position = position,
                movieName = movieName,
                slug = slug,
                onProgress = { index, fileName, progress ->
                    notificationHelper.updateProgress(
                        progress = progress,
                        movieName = movieName,
                        position = position!!
                    )
                    val intent = Intent(DownloadBroadcast.ACTION_PROGRESS).apply {
                        intent.setPackage(packageName)
                        putExtra(DownloadBroadcast.EXTRA_INDEX, index)
                        putExtra(DownloadBroadcast.EXTRA_PROGRESS, progress)
                    }
                    sendBroadcast(intent)
                    Log.d("aaaaaaa", "progress $progress")
                },
                onDownloadStart = { index, fileName ->

                },
                onDownloadComplete = { index, fileName, success ->
                    notificationHelper.complete(
                        movieName = movieName,
                        position = position,
                        success = success
                    )
                    val intent = Intent(DownloadBroadcast.ACTION_STATE).apply {
                        intent.setPackage(packageName)
                        putExtra(DownloadBroadcast.EXTRA_INDEX, index)
                        putExtra(DownloadBroadcast.EXTRA_STATE, DownloadState.DOWNLOADED.name)
                    }
                    sendBroadcast(intent)
                },
                onFinish = {
                    stopSelf()
                }
            )
        }
        return START_NOT_STICKY
    }
}

object DownloadBroadcast {
    const val ACTION_PROGRESS = "ACTION_PROGRESS"
    const val ACTION_STATE = "ACTION_STATE"
    const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
    const val EXTRA_INDEX = "EXTRA_INDEX"
    const val EXTRA_STATE = "EXTRA_STATE"
}
