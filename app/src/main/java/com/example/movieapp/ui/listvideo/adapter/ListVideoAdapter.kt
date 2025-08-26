package com.example.movieapp.ui.listvideo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movieapp.R
import com.example.movieapp.databinding.LayoutItemListMovieBinding
import com.example.movieapp.model.ServerData
import java.io.File


class ListVideoAdapter(
    private var list: ArrayList<ServerData>,
    var thumb: String? = "",
    var slug: String = "",
    var currentVideo: Int = 0,
    var onItemClick: ((Int) -> Unit)? = null,
    var onDownloadClick: (Int) -> Unit,
    var onDeleteClick: (Int) -> Unit,
    var onRemoveQUEUED: (Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: ArrayList<ServerData>, thumb: String?, slug: String) {
        this.list = list
        this.thumb = thumb
        this.slug = slug
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
            val item = list[position]
            binding.view.visibility = if (currentVideo == position) View.VISIBLE else View.INVISIBLE
            binding.imgPlay.visibility =
                if (currentVideo == position) View.VISIBLE else View.INVISIBLE
            Glide.with(binding.root.context).load(thumb).diskCacheStrategy(DiskCacheStrategy.ALL)
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

            when (item.downloadState) {
                DownloadState.IDLE -> {
                    binding.imgDownload.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE
                    binding.imgDownload.setImageResource(R.drawable.ic_download_white)
                    val exits = checkFileExits(binding.root.context, position)
                    if (exits) {
                        item.downloadState = DownloadState.DOWNLOADED
                        binding.imgDownload.setImageResource(R.drawable.ic_success)
                    }
                    binding.imgDownload.setOnClickListener {
                        onDownloadClick(position)
                    }
                }

                DownloadState.QUEUED -> {
                    binding.imgDownload.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE
                    binding.imgDownload.setImageResource(R.drawable.ic_download_gray)
                    binding.imgDownload.setOnClickListener {
                        onRemoveQUEUED(position)
                    }
                }

                DownloadState.DOWNLOADING -> {
                    binding.imgDownload.visibility = View.GONE
                    binding.progress.visibility = View.VISIBLE
                }

                DownloadState.DOWNLOADED -> {
                    binding.imgDownload.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE
                }
            }
            binding.download.setOnClickListener {
                Log.d("testing", "state")
                when (item.downloadState) {
                    DownloadState.IDLE -> {
                        item.downloadState = DownloadState.DOWNLOADING
                    }

                    DownloadState.QUEUED -> {
                        item.downloadState = DownloadState.IDLE
                    }

                    DownloadState.DOWNLOADING -> {
                        item.downloadState = DownloadState.IDLE
                    }

                    DownloadState.DOWNLOADED -> {
                        onDeleteClick(position)
                    }
                }
            }
        }
    }

    private fun setOnClickDelete(imgDownload: ImageView, position: Int) {
        imgDownload.setImageResource(R.drawable.ic_success)
        imgDownload.setOnClickListener {
            onDeleteClick(position)
        }
    }

    fun checkFileExits(context: Context, position: Int): Boolean {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "$slug/Tập${position + 1}.mp4"
        ).exists()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = LayoutItemListMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ListVideoViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListVideoViewHolder).bind(position)
    }

}

enum class DownloadState {
    IDLE, QUEUED, DOWNLOADING, DOWNLOADED
}