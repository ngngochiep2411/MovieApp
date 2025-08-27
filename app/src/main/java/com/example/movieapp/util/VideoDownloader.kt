package com.example.movieapp.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.example.movieapp.model.ServerData
import java.io.File

class VideoDownloader(
    val context: Context,

    ) {
    private val privateDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    private val queue = ArrayDeque<String>()
    var isDownloading = false


    init {
        privateDir?.mkdirs()
    }

    fun removeQueue(url: String) {
        queue.remove(url)
    }

    fun addQueue(url: String) {
        queue.add(url)
    }

    fun download(
        url: String,
        position: Int?,
        movieName: String,
        slug: String,
        onDownloadStart: (position: Int?, fileName: String) -> Unit,
        onProgress: (position: Int?, fileName: String, progress: Double) -> Unit,
        onDownloadComplete: (position: Int?, fileName: String, success: Boolean) -> Unit,
        onFinish: () -> Unit,
    ) {
        queue.add(url)
        if (!isDownloading) {
            processNext(
                onDownloadStart,
                onProgress,
                onDownloadComplete,
                onFinish,
                movieName,
                slug,
                position,
            )
        }
    }

    private fun processNext(
        onDownloadStart: (position: Int?, fileName: String) -> Unit,
        onProgress: (position: Int?, fileName: String, progress: Double) -> Unit,
        onDownloadComplete: (position: Int?, fileName: String, success: Boolean) -> Unit,
        onFinish: () -> Unit,
        movieName: String,
        slug: String,
        position: Int?
    ) {
        if (position != null && position >= queue.size) {
            onFinish()
            return
        }
        val task = queue.firstOrNull() ?: run {
            isDownloading = false
            onFinish()
            return
        }

        isDownloading = true
        val serverData = task

        val probeCmd = "-i $serverData -c copy -f null -"

        var waitingForDuration = false
        var videoDuration = ""
        var url = ""
        var started = false

        FFmpegKit.executeAsync(probeCmd, { session ->
            val returnCode = session.returnCode
            if (!ReturnCode.isSuccess(returnCode)) {
                removeQueue(url = url)
                processNext(
                    onDownloadStart,
                    onProgress,
                    onDownloadComplete,
                    onFinish,
                    movieName,
                    slug,
                    position
                )
            }
        }, { log ->
            val message = log.message
            if (message.contains("Opening") && message.contains(".m3u8")) {
                val regex = Regex("https?://[^\\s']+\\.m3u8")
                regex.find(message)?.let { url = it.value }
            }
            if (waitingForDuration) {
                val regex = Regex("""(?:\d{2}:)?\d{2}:\d{2}(?:\.\d{2})?""")
                regex.find(message)?.let { videoDuration = it.value }
                waitingForDuration = false
            }
            if (message.contains("Duration:")) waitingForDuration = true

            if (!started && url.isNotEmpty() && videoDuration.isNotEmpty()) {
                started = true
                Log.d("testing", "downloadvideo")
                val durationMs = parseDurationToMs(videoDuration)
                downloadVideo(
                    url = url,
                    movieName = movieName,
                    slug = slug,
                    position = position,
                    onDownloadStart = onDownloadStart,
                    onProgress = onProgress,
                    onDownloadComplete = { i, fileName, success ->
                        onDownloadComplete(i, fileName, success)
                        processNext(
                            onDownloadStart,
                            onProgress,
                            onDownloadComplete,
                            onFinish,
                            movieName,
                            slug,
                            position?.plus(1)
                        )
                    },
                    duration = durationMs
                )
            }
        }, { })
    }

    private fun downloadVideo(
        url: String,
        movieName: String,
        position: Int?,
        onDownloadStart: (position: Int?, fileName: String) -> Unit,
        onProgress: (position: Int?, fileName: String, progress: Double) -> Unit,
        onDownloadComplete: (position: Int?, fileName: String, success: Boolean) -> Unit,
        duration: Double,
        slug: String
    ) {


        val movieDir = File(privateDir, slug)
        if (movieDir.exists()) {
            movieDir.delete()
        }
        if (!movieDir.exists()) {
            movieDir.mkdirs()
        }
        val outputFile = File(movieDir, "Tập${position?.plus(1)}.mp4")
        val cmd = "-i $url -c copy -bsf:a aac_adtstoasc ${outputFile.absolutePath}"
        FFmpegKit.executeAsync(cmd, { session ->
            val returnCode = session.returnCode
            if (ReturnCode.isSuccess(returnCode)) {
                removeQueue(url = url)
                isDownloading = false
                Log.d("FFMPEGLOG", "Tải thành công: ${outputFile.absolutePath}")
                onDownloadComplete(position, movieName, true)
            } else {
                isDownloading = false
                Log.e("FFMPEGLOG", "Lỗi tải video: $returnCode")
                Log.e("FFMPEGLOG", "Output: ${session.allLogsAsString}")
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