package com.example.movieapp.ui.download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movieapp.databinding.LayoutItemDownloadBinding
import com.example.movieapp.model.VideoDownload

class VideoDownloadAdapter :
    ListAdapter<VideoDownload, VideoDownloadAdapter.VideoDownloadViewHolder>(DiffCallback) {

    inner class VideoDownloadViewHolder(
        val binding: LayoutItemDownloadBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(download: VideoDownload) {
            Glide.with(binding.root.context).load(download.thumb)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgThumb)
            binding.name.text = download.name
            binding.episode.text = download.slug
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
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
            override fun areItemsTheSame(oldItem: VideoDownload, newItem: VideoDownload): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: VideoDownload,
                newItem: VideoDownload
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
