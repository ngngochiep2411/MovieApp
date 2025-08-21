package com.example.movieapp.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.example.movieapp.model.ServerData
import java.io.File

class VideoDownloader(
    context: Context,

    ) {
    private val privateDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

    init {
        privateDir?.mkdirs()
    }

    fun downLoadVideo(
        list: ArrayList<ServerData>, slug: String,
        onDownloadStart: (index: Int, fileName: String) -> Unit,
        onProgress: (index: Int, fileName: String, progress: Int) -> Unit,
        onDownloadComplete: (index: Int, fileName: String, success: Boolean) -> Unit
    ) {
        for (i in list.indices) {
            val probeCmd = "-i ${list[i].linkM3u8} -c copy -f null -"
            FFmpegKit.executeAsync(
                probeCmd,
                { session ->
                    val returnCode = session.returnCode
                    if (ReturnCode.isSuccess(returnCode)) {

                    } else {

                    }
                },
                { log ->
                    val message = log.message
                    Log.d("FFMPEGLOG", message)
                    if (message.contains("Opening") && message.contains(".m3u8")) {
                        val regex = Regex("https?://[^\\s']+\\.m3u8")
                        val match = regex.find(message)
                        if (match != null) {
                            val url = match.value
                            downloadVideo(
                                url = url,
                                slug = slug,
                                i = i,
                                onDownloadStart = onDownloadStart,
                                onProgress = onProgress,
                                onDownloadComplete = onDownloadComplete
                            )
                        }
                    }
                },
                { stats ->
                    Log.d("FFMPEGLOG", "probeStats: $stats")
                }
            )
        }

    }

    private fun downloadVideo(
        url: String, slug: String, i: Int,
        onDownloadStart: (index: Int, fileName: String) -> Unit,
        onProgress: (index: Int, fileName: String, progress: Int) -> Unit,
        onDownloadComplete: (index: Int, fileName: String, success: Boolean) -> Unit
    ) {
        val movieDir = File(privateDir, slug)
        if (!movieDir.exists()) {
            movieDir.mkdirs()
        }
        val outputFile = File(movieDir, "${slug}_tập${i + 1}.mp4")
        val cmd = "-i $url -c copy ${outputFile.absolutePath}"
        onDownloadStart(i, slug)
        FFmpegKit.executeAsync(
            cmd,
            { session ->
                val returnCode = session.returnCode
                if (ReturnCode.isSuccess(returnCode)) {
                    Log.d("FFMPEGLOG", "Tải thành công: ${outputFile.absolutePath}")
                } else {
                    Log.e("FFMPEGLOG", "Lỗi tải video: $returnCode")
                }
            },
            {

                    log ->
                Log.d("FFMPEGLOG", "${log.message}")
            },
            { stats -> Log.d("FFMPEGLOG", "$stats") }
        )
    }
}