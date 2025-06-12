package com.example.movieapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.movieapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class Utils {
    companion object {

        private var loadingDialog: AlertDialog? = null


        fun showDialog(activity: Activity) {
            if (loadingDialog == null || !loadingDialog!!.isShowing) {
                val builder = AlertDialog.Builder(activity)
                builder.setView(R.layout.progress)
                loadingDialog = builder.create()
                loadingDialog?.show()
            }
        }

        fun dismissDialog() {
            if (loadingDialog != null && loadingDialog!!.isShowing) {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }

        @SuppressLint("Recycle")
        fun getRealPathFromURI(uri: Uri?, context: Context): String {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            uri?.let {
                val cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(projection[0])
                    return cursor.getString(columnIndex)
                }
            }

            return ""
        }

        fun getFileFromUri(context: Context, uri: Uri): File? {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "temp_file_${System.currentTimeMillis()}"
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return file
        }


        fun loadImage(context: Context, url: String?, imageView: ImageView) {
            val drawable = CircularProgressDrawable(context)
            drawable.setColorSchemeColors(
                R.color.primaryColor,
            )
            drawable.centerRadius = 30f
            drawable.strokeWidth = 5f
            drawable.start()
            Glide.with(context).load(url)
                .placeholder(drawable)
                .error(R.drawable.avatar_anonymous)
                .into(imageView)
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