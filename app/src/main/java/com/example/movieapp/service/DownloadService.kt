package com.example.movieapp.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
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
        if (intent?.action == ACTION_START) {
            val url = intent.getStringExtra(EXTRA_URL)
            val slug = intent.getStringExtra(EXTRA_SLUG)
            val movieName = intent.getStringExtra(EXTRA_MOVIE_NAME)
            val position = intent.getIntExtra(EXTRA_POSITION, -1)
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
                            position = position
                        )
                        val intent = Intent(ACTION_PROGRESS).apply {
                            intent.setPackage(packageName)
                            putExtra(EXTRA_INDEX, index)
                            putExtra(EXTRA_PROGRESS, progress)
                        }
                        sendBroadcast(intent)
                    },
                    onDownloadStart = { index, fileName ->
                        notificationHelper.updateProgress(
                            progress = 0.0,
                            movieName = movieName,
                            position = position
                        )
                        val intent = Intent(ACTION_STATE).apply {
                            intent.setPackage(packageName)
                            putExtra(EXTRA_INDEX, index)
                            putExtra(EXTRA_STATE, progress)
                        }
                        sendBroadcast(intent)
                    },
                    onDownloadComplete = { index, fileName, success ->
                        notificationHelper.complete(
                            movieName = movieName,
                            position = position,
                            success = success
                        )
                        val intent = Intent(ACTION_STATE).apply {
                            intent.setPackage(packageName)
                            putExtra(EXTRA_INDEX, index)
                            putExtra(EXTRA_STATE, DownloadState.DOWNLOADED.name)
                        }
                        sendBroadcast(intent)
                    },
                    onFinish = {
                        stopSelf()
                    }
                )
            }
        }

        return START_NOT_STICKY
    }

    companion object {
        const val ACTION_PROGRESS = "ACTION_PROGRESS"
        const val ACTION_STATE = "ACTION_STATE"
        const val ACTION_START = "ACTION_START"

        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        const val EXTRA_INDEX = "EXTRA_INDEX"
        const val EXTRA_STATE = "EXTRA_STATE"
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_SLUG = "EXTRA_SLUG"
        const val EXTRA_MOVIE_NAME = "EXTRA_MOVIE_NAME"
        const val EXTRA_POSITION = "EXTRA_POSITION"
    }

    enum class DownloadState {
        IDLE, QUEUED, DOWNLOADING, DOWNLOADED
    }
}
