package com.example.movieapp.ui.playlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.databinding.LayoutItemMoviePlayListBinding
import com.example.movieapp.model.Video
import com.example.movieapp.util.Utils

class VideoAdapter(
    private val onItemClick: (Video) -> Unit,
    private val onDelete: (Video) -> Unit,
) : ListAdapter<Video, VideoAdapter.VideoViewHolder>(DiffCallback) {


    inner class VideoViewHolder(
        val binding: LayoutItemMoviePlayListBinding,
        val onItemClick: (Video) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Video) {
            Glide.with(binding.imgThumb.context.applicationContext)
                .load(item.detailMovie.movie?.thumbUrl)
                .into(binding.imgThumb)
            binding.movieName.text = item.detailMovie.movie?.name
            Log.d("testingaaaa", "${item.detailMovie.movie?.thumbUrl}")
            binding.delete.setOnClickListener {
                onDelete(item)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutItemMoviePlayListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClick
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
            oldItem == newItem
    }
}