package com.example.movieapp.ui.listvideo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movieapp.R
import com.example.movieapp.databinding.LayoutFlexboxItemBinding
import com.example.movieapp.databinding.LayoutItemListMovieBinding
import com.example.movieapp.model.ServerData
import com.example.movieapp.util.Utils


class ListVideoAdapter2(
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

    inner class LayoutFlexboxViewHolder(private val binding: LayoutFlexboxItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val layoutParams = binding.root.layoutParams as GridLayoutManager.LayoutParams
            if ((position + 1) % 6 == 0) {
                layoutParams.setMargins(
                    0,
                    0,
                    0,
                    binding.root.context.resources.getDimension(R.dimen.dp4).toInt()
                )

            } else {
                layoutParams.setMargins(
                    0,
                    0,
                    binding.root.context.resources.getDimension(R.dimen.dp4).toInt(),
                    binding.root.context.resources.getDimension(R.dimen.dp4).toInt()
                )
            }
            binding.root.layoutParams = layoutParams

            binding.text.text = (position + 1).toString()
            if (currentVideo == position) {
                binding.text.setTextColor(binding.root.context.getColor(R.color.primaryColor))
            } else {
                binding.text.setTextColor(binding.root.context.getColor(R.color.white))
            }
            binding.root.setOnClickListener {
                onItemClick?.invoke(position)
                currentVideo = position
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            LayoutFlexboxItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return LayoutFlexboxViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LayoutFlexboxViewHolder).bind(position)
    }

}