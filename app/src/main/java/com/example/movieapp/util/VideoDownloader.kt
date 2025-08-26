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

    init {
        privateDir?.mkdirs()
    }

    fun downLoadVideo(
        list: ArrayList<ServerData>,
        movieName: String,
        slug: String,
        onDownloadStart: (index: Int, fileName: String) -> Unit,
        onProgress: (index: Int, fileName: String, progress: Double) -> Unit,
        onDownloadComplete: (index: Int, fileName: String, success: Boolean) -> Unit
    ) {
        fun processVideo(index: Int) {
            if (index >= list.size) {
                return
            }

            val serverData = list[index]
            val probeCmd = "-i ${serverData.linkM3u8} -c copy -f null -"

            var waitingForDuration = false
            var videoDuration = ""
            var url = ""
            var isDownloading = false

            FFmpegKit.executeAsync(probeCmd, { session ->
                val returnCode = session.returnCode
                if (!ReturnCode.isSuccess(returnCode)) {
                    processVideo(index + 1)
                }
            }, { log ->
                val message = log.message
                if (message.contains("Opening") && message.contains(".m3u8")) {
                    val regex = Regex("https?://[^\\s']+\\.m3u8")
                    val match = regex.find(message)
                    if (match != null) {
                        url = match.value
                    }
                }
                if (waitingForDuration) {
                    val regex = Regex("""(?:\d{2}:)?\d{2}:\d{2}(?:\.\d{2})?""")
                    val match = regex.find(message)
                    if (match != null) {
                        videoDuration = match.groupValues[0]
                    }
                    waitingForDuration = false
                }
                if (message.contains("Duration:")) {
                    waitingForDuration = true
                }

                if (url.isNotEmpty() && videoDuration.isNotEmpty() && !isDownloading) {
                    isDownloading = true
                    val durationMs = parseDurationToMs(videoDuration)
                    Log.d("testing", "durationMs: $durationMs")
                    downloadVideo(
                        url = url,
                        movieName = movieName,
                        slug = slug,
                        i = index,
                        onDownloadStart = onDownloadStart,
                        onProgress = onProgress,
                        onDownloadComplete = { i, fileName, success ->
                            onDownloadComplete(i, fileName, success)
                            processVideo(index + 1)
                        },
                        duration = durationMs
                    )
                }
            }, { stats ->
                Log.d("FFMPEGLOG", "probeStats: $stats")
            })
        }

        processVideo(0)
    }


    private fun downloadVideo(
        url: String,
        movieName: String,
        i: Int,
        onDownloadStart: (index: Int, fileName: String) -> Unit,
        onProgress: (index: Int, fileName: String, progress: Double) -> Unit,
        onDownloadComplete: (index: Int, fileName: String, success: Boolean) -> Unit,
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
        val outputFile = File(movieDir, "Tập${i + 1}.mp4")
        val cmd = "-i $url -c copy -bsf:a aac_adtstoasc ${outputFile.absolutePath}"
        FFmpegKit.executeAsync(cmd, { session ->
            val returnCode = session.returnCode
            if (ReturnCode.isSuccess(returnCode)) {
                Log.d("FFMPEGLOG", "Tải thành công: ${outputFile.absolutePath}")
                onDownloadComplete(i, movieName, true)
            } else {
                Log.e("FFMPEGLOG", "Lỗi tải video: $returnCode")
                Log.e("FFMPEGLOG", "Output: ${session.allLogsAsString}")
                onDownloadComplete(i, movieName, false)
            }
        }, { log ->
            val message = log.message
            Log.d("FFMPEGLOG", message)

        }, { statistics ->
            if (duration > 0) {
                val current = statistics.time
                val progress = (current / duration) * 100
                onProgress(i, movieName, progress)
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