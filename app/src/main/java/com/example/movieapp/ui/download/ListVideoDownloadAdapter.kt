package com.example.movieapp.ui.download

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movieapp.databinding.LayoutItemListMovieDownloadBinding
import com.example.movieapp.model.Film
import java.io.File

class ListVideoDownloadAdapter(
    var list: ArrayList<Film> = ArrayList(),
    var thumb: String? = "",
    var slug: String? = "",
    var currentVideo: Int = 0,
    var onItemClick: ((Int) -> Unit)? = null,
    val onDelete: ((Int) -> Unit)? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun submitList(list: ArrayList<Film>, thumb: String?, slug: String?) {
        this.list = list
        this.thumb = thumb
        this.slug = slug
        notifyDataSetChanged()
    }

    fun updateCurrentVideo(currentVideo: Int) {
        this.currentVideo = currentVideo
        this.notifyDataSetChanged()
    }

    inner class ListVideoViewHolder(private val binding: LayoutItemListMovieDownloadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = list[position]

            binding.view.visibility = if (currentVideo == position) View.VISIBLE else View.INVISIBLE
            binding.imgPlay.visibility =
                if (currentVideo == position) View.VISIBLE else View.INVISIBLE

            Glide.with(binding.root.context).load(thumb).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgThumb)

            binding.movieName.text = item.name
            binding.tvName.text = item.episode
            binding.root.setOnClickListener { onItemClick?.invoke(position) }
            binding.imgDelete.setOnClickListener {
                onDelete?.invoke(position)
            }
        }
    }


    fun checkFileExits(context: Context, position: Int): Boolean {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "$slug/Tập${position + 1}.mp4"
        ).exists()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = LayoutItemListMovieDownloadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ListVideoViewHolder(binding)
    }


    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        (holder as ListVideoViewHolder).bind(position)
    }
}