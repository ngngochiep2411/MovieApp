package com.example.movieapp.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.example.movieapp.util.DownloadCallback
import com.example.movieapp.util.DownloadTask
import com.example.movieapp.util.Notification
import com.example.movieapp.util.SendBroadCast.Companion.sendBroadCast
import com.example.movieapp.util.VideoDownloader

class DownloadService : Service() {

    private val binder = LocalBinder()
    lateinit var videoDownloader: VideoDownloader
    private lateinit var notificationHelper: Notification

    inner class LocalBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }


    fun getQueue(): List<DownloadTask> = videoDownloader.currentQueue()

    fun isDownloading(): Boolean {
        return videoDownloader.isDownloading
    }

    override fun onCreate() {
        videoDownloader = VideoDownloader(this)
        notificationHelper = Notification(this)

        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START) {
            val url = intent.getStringExtra(EXTRA_URL)
            val slug = intent.getStringExtra(EXTRA_SLUG)
            val movieName = intent.getStringExtra(EXTRA_MOVIE_NAME)
            val position = intent.getIntExtra(EXTRA_POSITION, -1)
            val downloadMode = intent.getStringExtra(EXTRA_DOWNLOAD_MODE)
            if (url != null && slug != null && movieName != null && downloadMode != null && position != -1) {
                val downloadTask = DownloadTask(
                    url = url,
                    position = position,
                    movieName = movieName,
                    slug = slug,
                    downloadMode = downloadMode
                )
                if (videoDownloader.isDownloading) {
                    videoDownloader.addQueue(
                        downloadTask = downloadTask
                    )
                    sendBroadCast(
                        context = this,
                        action = ACTION_UPDATE_STATE,
                        position = position,
                        state = DownloadState.QUEUED.name,
                        slug = slug
                    )
                } else {
                    startForeground(
                        notificationHelper.notificationId, notificationHelper.getBuilder().build()
                    )
                    videoDownloader.download(
                        downloadTask = downloadTask,
                        downloadCallback = downloadCallBack
                    )

                }
            }
        }

        if (intent?.action == ACTION_REMOVE_QUEUE) {
            videoDownloader.removeQueue(
                url = intent.getStringExtra(EXTRA_URL) ?: ""
            )
        }

        if (intent?.action == ACTION_CANCEL) {
            val slug = intent.getStringExtra(EXTRA_SLUG)
            val position = intent.getIntExtra(EXTRA_POSITION, -1)
            if (slug != null && position != -1) {
                videoDownloader.cancelDownload()
                videoDownloader.deleteFile(slug = slug, position = position)
            }
        }
        return START_STICKY
    }

    val downloadCallBack = object : DownloadCallback {
        override fun onStart(position: Int, movieName: String, slug: String?) {
            notificationHelper.onStart(
                progress = 0.0, movieName = movieName, position = position, slug = slug
            )
            sendBroadCast(
                context = this@DownloadService,
                action = ACTION_UPDATE_STATE,
                position = position,
                state = DownloadState.DOWNLOADING.name,
                slug = slug
            )
        }

        override fun onProgress(position: Int, movieName: String, progress: Double, slug: String?) {
            notificationHelper.updateProgress(
                progress = progress, movieName = movieName, position = position, slug = slug
            )
            sendBroadCast(
                context = this@DownloadService,
                action = ACTION_UPDATE_PROGRESS,
                position = position,
                state = DownloadState.DOWNLOADING.name,
                slug = slug
            )
        }

        override fun onComplete(position: Int, movieName: String, success: Boolean, slug: String?) {
            notificationHelper.complete(
                movieName = movieName, position = position, success = success
            )
            sendBroadCast(
                context = this@DownloadService,
                action = ACTION_UPDATE_STATE,
                position = position,
                state = DownloadState.DOWNLOADED.name,
                slug = slug
            )
        }

        override fun onFinish() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            stopSelf()
        }
    }


    companion object {

        const val ACTION_CANCEL = "ACTION_CANCEL"
        const val ACTION_UPDATE_PROGRESS = "ACTION_UPDATE_PROGRESS"
        const val ACTION_UPDATE_STATE = "ACTION_UPDATE_STATE"
        const val ACTION_START = "ACTION_START"
        const val ACTION_REMOVE_QUEUE = "ACTION_REMOVE_QUEUE"

        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        const val EXTRA_INDEX = "EXTRA_INDEX"
        const val EXTRA_STATE = "EXTRA_STATE"
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_SLUG = "EXTRA_SLUG"
        const val EXTRA_MOVIE_NAME = "EXTRA_MOVIE_NAME"
        const val EXTRA_DOWNLOAD_MODE = "EXTRA_DOWNLOAD_MODE"
        const val EXTRA_POSITION = "EXTRA_POSITION"
    }

    enum class DownloadState {
        IDLE, QUEUED, DOWNLOADING, DOWNLOADED
    }
}
