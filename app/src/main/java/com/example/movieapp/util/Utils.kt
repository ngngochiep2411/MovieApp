package com.example.movieapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class Utils {
    companion object {


        fun uriToFile(uri: Uri, context: Context): File? {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)

            val file =
                File(context.cacheDir, "temp_file") // Tạo file tạm trong bộ nhớ cache của ứng dụng
            try {
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                outputStream.close()
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return file
        }

        fun AppCompatActivity.transparentStatusBar() {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }


        fun AppCompatActivity.changeStatusBarColor() {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.statusBarColor = Color.BLACK
        }

        fun getScreenWidth(context: Context): Int {
            val displayMetrics = DisplayMetrics()
            val display =
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            display.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        fun getScreenHeight(context: Context): Int {
            val displayMetrics = DisplayMetrics()
            val display =
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            display.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

        private fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }

        @SuppressLint("InternalInsetResource")
        fun getStatusBarHeight(context: Context): Int {
            val resourceId: Int =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
        }


        private suspend fun resolveRedirectUrl(urlString: String): String {
            return withContext(Dispatchers.IO) {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.instanceFollowRedirects = false
                connection.connect()


                if (connection.responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    connection.responseCode == HttpURLConnection.HTTP_MOVED_PERM
                ) {
                    val newUrl = connection.getHeaderField("Location")
                    connection.disconnect()
                    return@withContext newUrl
                }

                connection.disconnect()
                return@withContext urlString
            }
        }
    }


}