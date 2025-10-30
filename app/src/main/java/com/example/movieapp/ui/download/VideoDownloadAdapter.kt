package com.example.movieapp.ui.download

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movieapp.databinding.LayoutItemDownloadBinding
import com.example.movieapp.model.VideoDownload
import java.io.File

class VideoDownloadAdapter(
    val onItemClick: (VideoDownload) -> Unit,
    val onDelete: (VideoDownload, Int) -> Unit,
) :
    ListAdapter<VideoDownload, VideoDownloadAdapter.VideoDownloadViewHolder>(DiffCallback) {

    inner class VideoDownloadViewHolder(
        val binding: LayoutItemDownloadBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(download: VideoDownload) {
            Glide.with(binding.root.context).load(download.thumb)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgThumb)
            binding.name.text = download.name
            binding.episode.text = "${countMp4Files(binding.root.context, download.slug)} tập"
            binding.root.setOnClickListener {
                onItemClick(download)
            }
            binding.delete.setOnClickListener {
                onDelete(download, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): VideoDownloadViewHolder {
        val binding = LayoutItemDownloadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VideoDownloadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoDownloadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<VideoDownload>() {
            override fun areItemsTheSame(
                oldItem: VideoDownload,
                newItem: VideoDownload
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: VideoDownload, newItem: VideoDownload
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

fun countMp4Files(context: Context, slug: String): Int {
    val privateDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), slug)
    if (!privateDir.exists() || !privateDir.isDirectory) {
        return 0
    }
    val mp4Files = privateDir.listFiles { file ->
        file.isFile && file.extension.equals("mp4", ignoreCase = true)
    }
    return mp4Files?.size ?: 0
}
