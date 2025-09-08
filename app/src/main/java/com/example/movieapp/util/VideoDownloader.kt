package com.example.movieapp.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File

class VideoDownloader(
    val context: Context,

    ) {
    private val privateDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    private val queue = ArrayDeque<DownloadTask>()
    var isDownloading = false
    private var session: FFmpegSession? = null


    init {
        privateDir?.mkdirs()
    }

    fun removeQueue(url: String?) {
        val removed = queue.removeIf { it.url == url }
        if (removed) {
            Log.d("DownloadQueue", "Removed task with url=$url")
        } else {
            Log.d("DownloadQueue", "No task found with url=$url")
        }
    }


    fun addQueue(downloadTask: DownloadTask) {
        val exits = queue.any { it.url == downloadTask.url }
        if (!exits) {
            queue.add(downloadTask)
        }
        queue.forEachIndexed { index, task ->
            Log.d(
                "VideoDownloader",
                "[$index] url=${task.url}, slug=${task.slug}, movieName=${task.movieName}, position=${task.position}"
            )
        }
    }

    fun download(
        onDownloadStart: (position: Int, fileName: String) -> Unit,
        onProgress: (position: Int, fileName: String, progress: Double) -> Unit,
        onDownloadComplete: (position: Int, fileName: String, success: Boolean) -> Unit,
        onFinish: () -> Unit,
        downloadTask: DownloadTask
    ) {
        addQueue(downloadTask)
        if (!isDownloading) {
            processNext(
                onDownloadStart = onDownloadStart,
                onProgress = onProgress,
                onDownloadComplete = onDownloadComplete,
                onFinish = onFinish
            )
        }
        isDownloading = true
    }

    private fun processNext(
        onDownloadStart: (position: Int, fileName: String) -> Unit,
        onProgress: (position: Int, fileName: String, progress: Double) -> Unit,
        onDownloadComplete: (position: Int, fileName: String, success: Boolean) -> Unit,
        onFinish: () -> Unit
    ) {
        if (queue.isEmpty()) {
            onFinish()
            return
        }
        val task = queue.firstOrNull() ?: run {
            isDownloading = false
            onFinish()
            return
        }

        onDownloadStart(task.position, task.movieName)
        val logUrl = task.url
        val probeCmd = "-i $logUrl -c copy -f null -"

        var waitingForDuration = false
        var videoDuration = ""
        var downloadUrl = ""
        var started = false

        FFmpegKit.executeAsync(probeCmd, { session ->
            val returnCode = session.returnCode
            if (!ReturnCode.isSuccess(returnCode)) {
                removeQueue(logUrl)
                processNext(
                    onDownloadStart = onDownloadStart,
                    onProgress = onProgress,
                    onDownloadComplete = onDownloadComplete,
                    onFinish = onFinish,
                )
            } else {
                Log.e("FFMPEGLOG", "Lỗi khi tải video: $returnCode")
                Log.e("FFMPEGLOG", "Fail stack trace: ${session.failStackTrace}")
            }
        }, { log ->
            val message = log.message
            Log.d("zmmsfdsfs", "$message")
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
                started = true
                Log.d("testing", "downloadvideo")
                val durationMs = parseDurationToMs(videoDuration)
                downloadVideo(
                    url = task.url,
                    downloadUrl = downloadUrl,
                    movieName = task.movieName,
                    slug = task.slug,
                    position = task.position,
                    onDownloadStart = onDownloadStart,
                    onProgress = onProgress,
                    onDownloadComplete = { i, fileName, success ->
                        onDownloadComplete(i, fileName, success)
                        processNext(
                            onDownloadStart, onProgress, onDownloadComplete, onFinish
                        )
                    },
                    duration = durationMs
                )
            }
        }, {
            Log.e("FFMPEGLOG", "$it")
        })
    }

    private fun downloadVideo(
        downloadUrl: String,
        movieName: String,
        position: Int,
        onDownloadStart: (position: Int, fileName: String) -> Unit,
        onProgress: (position: Int, fileName: String, progress: Double) -> Unit,
        onDownloadComplete: (position: Int, fileName: String, success: Boolean) -> Unit,
        duration: Double,
        slug: String,
        url: String?
    ) {


        val movieDir = File(privateDir, slug)
        if (movieDir.exists()) {
            movieDir.delete()
        }
        if (!movieDir.exists()) {
            movieDir.mkdirs()
        }
        val outputFile = File(movieDir, "Tập${position?.plus(1)}.mp4")
        val cmd = "-i $downloadUrl -c copy -bsf:a aac_adtstoasc ${outputFile.absolutePath}"
        session = FFmpegKit.executeAsync(cmd, { session ->
            val returnCode = session.returnCode
            if (ReturnCode.isSuccess(returnCode)) {
                removeQueue(url)
                isDownloading = false
                Log.d("FFMPEGLOG", "Tải thành công: ${outputFile.absolutePath}")
                onDownloadComplete(position, movieName, true)
            } else {
                isDownloading = false
                Log.e("FFMPEGLOG", "Lỗi tải video: $returnCode")
                Log.e("FFMPEGLOG", "Output: ${session.allLogsAsString}")
                removeQueue(url)
                onDownloadComplete(position, movieName, false)
            }
        }, { log ->
            val message = log.message
            Log.d("FFMPEGLOG", message)

        }, { statistics ->

            if (duration > 0) {
                val current = statistics.time
                val progress = (current / duration) * 100
                onProgress(position, movieName, progress)
                Log.d("FFMPEGLOG", "Progress: $progress%")
            }
        })
    }

    fun cancelDownload() {
        session?.cancel()
    }

    fun deleteFile(downloadTask: DownloadTask) {
        try {
            val movieDir = File(privateDir, downloadTask.slug)
            val outputFile = File(movieDir, "Tập${downloadTask.position + 1}.mp4")
            if (outputFile.exists()) {
                outputFile.delete()
            }
        } catch (e: Exception) {
            Log.e("DeleteFile", "Lỗi khi xóa file", e)
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

data class DownloadTask(
    val url: String, val position: Int, val movieName: String, val slug: String
)
