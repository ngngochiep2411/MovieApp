package com.example.movieapp.util

import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.movieapp.service.DownloadService

class SendBroadCast {

    companion object {


        fun startService(
            context: Context,
            act: String,
            url: String = "",
            slug: String? = "",
            movieName: String? = null,
            position: Int = -1,
            downloadMode: String = ""
        ) {
            val intent = Intent(context, DownloadService::class.java).apply {
                setPackage(context.packageName)
                action = act
                if (url.isNotEmpty()) {
                    putExtra(DownloadService.EXTRA_URL, url)
                }
                if (!slug.isNullOrEmpty()) {
                    putExtra(DownloadService.EXTRA_SLUG, slug)
                }
                if (!movieName.isNullOrEmpty()) {
                    putExtra(DownloadService.EXTRA_MOVIE_NAME, movieName)
                }
                if (downloadMode.isNotEmpty()) {
                    putExtra(DownloadService.EXTRA_DOWNLOAD_MODE, downloadMode)
                }
                if (position != -1) {
                    putExtra(DownloadService.EXTRA_POSITION, position)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun sendBroadCast(
            context: Context,
            action: String,
            state: String? = null,
            position: Int = -1,
            url: String = "",
            slug: String? = "",
            movieName: String? = null
        ) {
            val intent = Intent(action).apply {
                setPackage(context.packageName)
                if (state != null) {
                    putExtra(DownloadService.EXTRA_STATE, state)
                }
                if (position != -1) {
                    putExtra(DownloadService.EXTRA_INDEX, position)
                }
                if (url.isNotEmpty()) {
                    putExtra(DownloadService.EXTRA_URL, url)
                }
                if (!slug.isNullOrEmpty()) {
                    putExtra(DownloadService.EXTRA_SLUG, slug)
                }
                if (!movieName.isNullOrEmpty()) {
                    putExtra(DownloadService.EXTRA_MOVIE_NAME, slug)
                }
            }
            context.sendBroadcast(intent)
        }
    }


}