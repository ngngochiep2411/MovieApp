package com.example.movieapp.ui.detailcategory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.LayoutItemMovieSearchBinding
import com.example.movieapp.databinding.LayoutLoadingBinding
import com.example.movieapp.model.Movie
import com.example.movieapp.model.MultiViewItem
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.util.Utils

class DetailCategoryAdapter() :
    androidx.recyclerview.widget.ListAdapter<MultiViewItem, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<MultiViewItem>() {
        override fun areItemsTheSame(oldItem: MultiViewItem, newItem: MultiViewItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: MultiViewItem, newItem: MultiViewItem): Boolean {
            return oldItem == newItem
        }

    }) {

    inner class LoadingViewHolder(binding: LayoutLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
        }

    }

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
        return when (viewType) {
            0 -> {
                MovieViewHolder(
                    LayoutItemMovieSearchBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            else -> LoadingViewHolder(
                LayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val item = getItem(position) as Movie
            holder.bind(item)
        } else if (holder is LoadingViewHolder) {
            holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is Movie) {
            0
        } else {
            1
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}