package com.example.movieapp.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.LayoutItemMovieBinding
import com.example.movieapp.model.Movie
import com.example.movieapp.model.MultiViewItem
import com.example.movieapp.util.Utils

class MovieAdapter(
    private val isSeriesMovie: Boolean,
    private val setWidth: Boolean = true,
) : ListAdapter<MultiViewItem, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<MultiViewItem>() {
    override fun areItemsTheSame(oldItem: MultiViewItem, newItem: MultiViewItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: MultiViewItem, newItem: MultiViewItem): Boolean {
        return oldItem == newItem
    }

}) {


    inner class MovieViewHolder(private val binding: LayoutItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {

            Utils.loadImage(binding.root.context, "https://phimimg.com/" + movie.thumbUrl, binding.imgThumb)
            binding.movieName.text = movie.name
            if (isSeriesMovie) {
                binding.tvEpisodeCurrent.visibility = View.VISIBLE
                binding.tvTotalTime.visibility = View.INVISIBLE
                binding.tvEpisodeCurrent.text = movie.episodeCurrent
            } else {
                binding.tvTotalTime.visibility = View.VISIBLE
                binding.tvEpisodeCurrent.visibility = View.INVISIBLE
                binding.tvTotalTime.text = movie.time
            }

            if (setWidth) {
                binding.root.layoutParams.width =
                    (Utils.getScreenWidth(binding.root.context) / (2.5) - 20).toInt()
            }

            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(position = adapterPosition, name = movie.slug)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(
            LayoutItemMovieBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            currentList[position]?.let { holder.bind(it as Movie) }
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}

