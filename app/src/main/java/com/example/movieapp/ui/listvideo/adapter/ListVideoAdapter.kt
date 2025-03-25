package com.example.movieapp.ui.listvideo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movieapp.databinding.LayoutItemListMovieBinding
import com.example.movieapp.model.ServerData


class ListVideoAdapter(
    private var list: ArrayList<ServerData>,
    var thumb: String? = "",
    var currentVideo: Int = 0,
    var onItemClick: ((Int) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: ArrayList<ServerData>, thumb: String?) {
        this.list = list
        this.thumb = thumb
        this.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCurrentVideo(currentVideo: Int) {
        this.currentVideo = currentVideo
        this.notifyDataSetChanged()
    }

    inner class ListVideoViewHolder(private val binding: LayoutItemListMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.view.visibility = if (currentVideo == position) View.VISIBLE else View.INVISIBLE
            binding.imgPlay.visibility =
                if (currentVideo == position) View.VISIBLE else View.INVISIBLE
            Glide.with(binding.root.context)
                .load(thumb)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgThumb)
            binding.tvName.text = list[position].name
            val fullName = list[position].filename?.split(" - ")
            if (fullName != null && fullName.size > 1) {
                val name = fullName[0] + " - " + fullName[1]
                binding.movieName.text = name
            } else if (fullName != null && fullName.size == 1) {
                binding.movieName.text = fullName[0]
            }
            binding.root.setOnClickListener {
                onItemClick?.invoke(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            LayoutItemListMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ListVideoViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListVideoViewHolder).bind(position)
    }

}