package com.example.movieapp.ui.download

import android.content.Intent
import android.os.Bundle
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


        adapter = VideoDownloadAdapter(::onItemClick)
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


}

