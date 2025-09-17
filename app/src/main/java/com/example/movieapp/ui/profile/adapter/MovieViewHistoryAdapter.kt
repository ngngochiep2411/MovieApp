package com.example.movieapp.ui.profile.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.LayoutItemMovieBinding
import com.example.movieapp.databinding.LayoutItemMovieHistoryBinding
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.util.Utils

class MovieViewHistoryAdapter() : ListAdapter<MovieHistory, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<MovieHistory>() {
    override fun areItemsTheSame(
        oldItem: MovieHistory,
        newItem: MovieHistory
    ): Boolean {
        return oldItem.slug == newItem.slug
    }

    override fun areContentsTheSame(
        oldItem: MovieHistory,
        newItem: MovieHistory
    ): Boolean {
        return oldItem == newItem
    }

}) {


    inner class MovieViewHistoryViewHolder(val binding: LayoutItemMovieHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieHistory?) {

            Utils.loadImage(
                binding.root.context,
                movie?.thumbUrl,
                binding.imgThumb
            )
            binding.movieName.text = movie?.name

            binding.tvTotalTime.text = movie?.total
                ?: "00:00"

            val progress =
                (movie?.watchedAt ?: 0).toDouble() / (movie?.duration ?: 0).toDouble() * 100
            binding.progressBar.progress = (progress).toInt()
            binding.episode.text = "Xem đến tập ${(movie?.episode)?.plus(1)}"
            binding.root.layoutParams.width =
                (Utils.getScreenWidth(binding.root.context) / (2.5) - 20).toInt()

            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(position = adapterPosition, name = "")
            }
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return MovieViewHistoryViewHolder(
            LayoutItemMovieHistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as MovieViewHistoryViewHolder).bind(currentList[position])
    }
}