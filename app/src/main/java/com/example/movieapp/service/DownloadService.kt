package com.example.movieapp.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.movieapp.model.ServerData
import com.example.movieapp.util.Notification
import com.example.movieapp.util.VideoDownloader

class DownloadService : Service() {

    private val binder = LocalBinder()
    private lateinit var videoDownloader: VideoDownloader
    private lateinit var notificationHelper: Notification

    inner class LocalBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
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
        val list: ArrayList<ServerData>? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableArrayListExtra("urls", ServerData::class.java)
            } else {
                @Suppress("DEPRECATION") intent?.getParcelableArrayListExtra("urls")
            }
        val slug = intent?.getStringExtra("slug")
        val movieName = intent?.getStringExtra("movieName")
        if (list != null && slug != null && movieName != null) {
            videoDownloader.downLoadVideo(
                list = list,
                movieName = movieName,
                slug = slug,
                onProgress = { index, fileName, progress ->
                    notificationHelper.updateProgress(
                        progress = progress,
                        movieName = movieName,
                        current = index
                    )
                    val intent = Intent(DownloadBroadcast.ACTION_PROGRESS).apply {
                        intent.setPackage(packageName)
                        putExtra(DownloadBroadcast.EXTRA_INDEX, index)
                        putExtra(DownloadBroadcast.EXTRA_PROGRESS, progress)
                    }
                    sendBroadcast(intent)
                    Log.d("aaaaaaa","progress $progress")
                },
                onDownloadStart = { index, fileName ->

                },
                onDownloadComplete = { index, fileName, success ->
                    notificationHelper.complete(
                        movieName = movieName,
                        current = index,
                        success = success
                    )
                }
            )

        }
        return START_NOT_STICKY
    }
}

object DownloadBroadcast {
    const val ACTION_PROGRESS = "com.example.movieapp.DOWNLOAD_PROGRESS"
    const val ACTION_COMPLETE = "com.example.movieapp.DOWNLOAD_COMPLETE"

    const val EXTRA_INDEX = "extra_index"
    const val EXTRA_FILENAME = "extra_filename"
    const val EXTRA_PROGRESS = "extra_progress"
    const val EXTRA_SUCCESS = "extra_success"
}
