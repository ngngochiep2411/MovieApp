package com.example.movieapp.ui.download

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityVideoDownloadBinding
import com.example.movieapp.model.VideoDownload
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class VideoDownloadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoDownloadBinding
    private lateinit var adapter: VideoDownloadAdapter
    private val viewModel: VideoDownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        viewModel.getVideoDownload()


        adapter = VideoDownloadAdapter(onItemClick = ::onItemClick, onDelete = ::onDelete)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            launch {
                viewModel.videoDownload.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    fun onItemClick(videoDownload: VideoDownload) {
        val intent = Intent(this, PlayVideoDownloadActivity::class.java)
        intent.putExtra("videoDownload", videoDownload)
        startActivity(intent)
    }

    fun onDelete(videoDownload: VideoDownload, position: Int) {
        showDialog(
            title = "Xóa nội dung đã tải?",
            message = "Bạn có muốn xóa nội dung đã tải xuống này không?",
            onAccept = {
                deleteFolder(videoDownload.slug, position)
            })
    }

    private fun deleteFolder(slug: String, position: Int) {
        viewModel.deleteFile(
            file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), slug),
            position = position,
            slug,
            onSuccess = { position ->
                val newList = adapter.currentList.toMutableList()
                newList.removeAt(position)
                adapter.submitList(newList)
            }
        )
    }

    private fun showDialog(
        title: String,
        message: String,
        onAccept: () -> Unit,
    ) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_dialog_confirm)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.findViewById<TextView>(R.id.cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.accept).setOnClickListener {
            dialog.dismiss()
            onAccept()
        }
        dialog.findViewById<TextView>(R.id.title).text = title
        dialog.findViewById<TextView>(R.id.message).text = message
        dialog.show()

    }
}

