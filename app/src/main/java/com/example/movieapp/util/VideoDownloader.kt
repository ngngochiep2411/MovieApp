package com.example.movieapp.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.example.movieapp.service.DownloadService
import com.example.movieapp.util.SendBroadCast.Companion.sendBroadCast
import java.io.File
import java.text.Normalizer

class VideoDownloader(
    val context: Context,

    ) {
    private val privateDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    private val queue = ArrayDeque<DownloadTask>()
    var isDownloading = false
    private var currentSension: FFmpegSession? = null
    private var currentTask: DownloadTask? = null
    var probeSession: FFmpegSession? = null

    init {
        privateDir?.mkdirs()
    }

    fun currentQueue(): List<DownloadTask> = queue.toList()

    fun removeQueue(url: String?) {
        queue.removeIf { it.url == url }
    }


    fun addQueue(downloadTask: DownloadTask) {
        val exits = queue.any { it.url == downloadTask.url }
        if (!exits) {
            queue.add(downloadTask)
        }
    }

    fun download(
        downloadTask: DownloadTask, downloadCallback: DownloadCallback
    ) {
        addQueue(downloadTask)
        if (!isDownloading) {
            isDownloading = true
            processNext(
                downloadCallBack = downloadCallback
            )
        }
    }

    private fun processNext(
        downloadCallBack: DownloadCallback
    ) {
        if (queue.isEmpty()) {
            isDownloading = false
            downloadCallBack.onFinish()
            return
        }
        val task = queue.firstOrNull() ?: run {
            isDownloading = false
            downloadCallBack.onFinish()
            return
        }
        currentTask = task
        downloadCallBack.onStart(task.position, task.movieName, task.slug)
        val logUrl = task.url
        val probeCmd = "-i $logUrl -c copy -f null -"

        var waitingForDuration = false
        var videoDuration = ""
        var downloadUrl = ""
        var started = false



        probeSession = FFmpegKit.executeAsync(probeCmd, { session ->

        }, { log ->
            val message = log.message
            if (message.contains("Opening") && message.contains(".m3u8")) {
                val regex = Regex("https?://[^\\s']+\\.m3u8")
                regex.find(message)?.let { downloadUrl = it.value }
            }
            if (waitingForDuration) {
                val regex = Regex("""(?:\d{2}:)?\d{2}:\d{2}(?:\.\d{2})?""")
                regex.find(message)?.let { videoDuration = it.value }
                waitingForDuration = false
            }
            if (message.contains("Duration:")) waitingForDuration = true

            if (!started && downloadUrl.isNotEmpty() && videoDuration.isNotEmpty()) {
                probeSession?.cancel()
                started = true
                val durationMs = parseDurationToMs(videoDuration)
                downloadVideo(
                    url = task.url,
                    downloadUrl = downloadUrl,
                    movieName = task.movieName,
                    slug = task.slug,
                    position = task.position,
                    duration = durationMs,
                    downloadCallback = downloadCallBack,
                    downloadMode = task.downloadMode
                )
            }
        }, {
        })
    }

    fun sanitizeFileName(name: String): String {
        var result = Normalizer.normalize(name, Normalizer.Form.NFD)
        result = result.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        result = result.replace(Regex("[\\\\/:*?\"<>|]"), "")
        result = result.replace(" ", "")
        return result
    }

    private fun downloadVideo(
        downloadUrl: String,
        movieName: String,
        position: Int,
        duration: Double,
        slug: String,
        url: String?,
        downloadMode: String,
        downloadCallback: DownloadCallback,
    ) {
        val movieDir = File(privateDir, slug)
        if (movieDir.exists()) {
            movieDir.delete()
        }
        if (!movieDir.exists()) {
            movieDir.mkdirs()
        }
        val outputFile =
            File(movieDir, "Tập${position.plus(1)}_${sanitizeFileName(downloadMode)}.mp4")
        val cmd = "-i $downloadUrl -c copy -bsf:a aac_adtstoasc ${outputFile.absolutePath}"
        val session = FFmpegKit.executeAsync(cmd, { session ->
            val returnCode = session.returnCode
            if (ReturnCode.isCancel(returnCode)) {
                sendBroadCast(
                    context = context,
                    action = DownloadService.ACTION_UPDATE_STATE,
                    position = position,
                    state = DownloadService.DownloadState.IDLE.name,
                    slug = slug
                )
                removeQueue(url)
                processNext(downloadCallBack = downloadCallback)
            } else if (ReturnCode.isSuccess(returnCode)) {
                removeQueue(url)
                downloadCallback.onComplete(position, movieName, true, slug)
                processNext(downloadCallBack = downloadCallback)
            } else if (!ReturnCode.isSuccess(returnCode)) {
                removeQueue(url)
                processNext(downloadCallBack = downloadCallback)
            }
        }, { log ->
            val message = log.message
            Log.e("testing", message)
        }, { statistics ->
            if (duration > 0) {
                val current = statistics.time
                val progress = (current / duration) * 100
                downloadCallback.onProgress(position, movieName, progress, slug)
            }
        })

        this.currentSension = session
    }

    fun cancelDownload() {
        currentSension?.cancel()
        currentSension = null
    }

    fun deleteFile(slug: String, position: Int) {
        try {
            val movieDir = File(privateDir, slug)
            val outputFile = File(movieDir, "Tập${position + 1}.mp4")
            if (outputFile.exists()) {
                outputFile.delete()
            }
        } catch (e: Exception) {
        }
    }


    fun parseDurationToMs(duration: String): Double {
        val parts = duration.split(":", ".")
        return when (parts.size) {
            4 -> { // hh:mm:ss.xx
                val hours = parts[0].toDouble()
                val minutes = parts[1].toDouble()
                val seconds = parts[2].toDouble()
                val centis = parts[3].toDouble() // 2 chữ số sau dấu chấm = centiseconds
                (hours * 3600 + minutes * 60 + seconds) * 1000 + centis * 10.0
            }

            3 -> { // mm:ss.xx hoặc hh:mm:ss (không có centisecond)
                val minutes = parts[0].toDouble()
                val seconds = parts[1].toDouble()
                val centis = parts[2].toDoubleOrNull() ?: 0.0
                (minutes * 60 + seconds) * 1000 + centis * 10.0
            }

            else -> 0.0
        }
    }
}

interface DownloadCallback {
    fun onStart(position: Int, movieName: String, slug: String?)
    fun onProgress(position: Int, movieName: String, progress: Double, slug: String?)
    fun onComplete(position: Int, movieName: String, success: Boolean, slug: String?)
    fun onFinish()
}

data class DownloadTask(
    val url: String,
    val position: Int,
    val movieName: String,
    val slug: String,
    val downloadMode: String
)
