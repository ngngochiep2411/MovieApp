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
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    }

}