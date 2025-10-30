package com.example.movieapp.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.LayoutItemMovieBinding
import com.example.movieapp.databinding.LayoutItemMovieSearchBinding
import com.example.movieapp.model.Movie
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.util.Utils

class SearchAdapter(
) : ListAdapter<Movie, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id === newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }

}) {

    inner class MovieViewHolder(private val binding: LayoutItemMovieSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            Utils.loadImage(
                binding.root.context,
                "https://phimimg.com/" + movie.thumbUrl,
                binding.imgThumb
            )
            binding.movieName.text = movie.name
            if (movie.episodeCurrent.toString() != "Full") {
                binding.tvEpisodeCurrent.visibility = View.VISIBLE
                binding.tvTotalTime.visibility = View.INVISIBLE
                binding.tvEpisodeCurrent.text = movie.episodeCurrent
            } else {
                binding.tvTotalTime.visibility = View.VISIBLE
                binding.tvEpisodeCurrent.visibility = View.INVISIBLE
                binding.tvTotalTime.text = movie.time
            }


            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(position = adapterPosition, name = movie.slug)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(
            LayoutItemMovieSearchBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieViewHolder).bind(getItem(position))
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}