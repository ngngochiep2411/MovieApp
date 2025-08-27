package com.example.movieapp.ui.listvideo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movieapp.R
import com.example.movieapp.databinding.LayoutItemListMovieBinding
import com.example.movieapp.model.ServerData
import com.example.movieapp.service.DownloadBroadcast
import java.io.File


class ListVideoAdapter(
    var list: ArrayList<ServerData>,
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

            binding.tvName.text = item.name
            val fullName = item.filename?.split(" - ")
            binding.movieName.text = when {
                fullName != null && fullName.size > 1 -> fullName[0] + " - " + fullName[1]
                fullName != null && fullName.size == 1 -> fullName[0]
                else -> ""
            }

            binding.root.setOnClickListener { onItemClick?.invoke(position) }

            setImageDrawable(item)

            binding.download.setOnClickListener {
                when (item.downloadState) {
                    DownloadState.IDLE -> {
                        onDownloadClick(position)
                    }

                    DownloadState.QUEUED -> {
                        item.downloadState = DownloadState.IDLE
                    }

                    DownloadState.DOWNLOADING -> {
                    }

                    DownloadState.DOWNLOADED -> {
                        onDeleteClick(position)
                    }
                }
            }
        }

        fun setImageDrawable(item: ServerData) {
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
                    binding.progressBar.progress =
                        item.progress.toFloat()   // hiển thị progress hiện tại
                }

                DownloadState.DOWNLOADED -> {
                    binding.imgDownload.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE
                    binding.imgDownload.setImageResource(R.drawable.ic_success)
                }
            }
        }

        fun updateProgressOnly(progress: Int) {
            binding.progressBar.progress = progress.toFloat()
        }

        fun updateState(item: ServerData) {
            setImageDrawable(item)
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

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                when (payload) {
                    DownloadBroadcast.EXTRA_PROGRESS -> {
                        val item = list[position]
                        val viewHolder = holder as ListVideoViewHolder
                        viewHolder.updateProgressOnly(item.progress)
                    }

                    DownloadBroadcast.EXTRA_STATE -> {
                        val item = list[position]
                        val viewHolder = holder as ListVideoViewHolder
                        viewHolder.updateState(item)
                    }

                    else -> (holder as ListVideoViewHolder).bind(position)
                }
            }
        } else {
            (holder as ListVideoViewHolder).bind(position)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        (holder as ListVideoViewHolder).bind(position)
    }

    fun updateProgress(index: Int, progress: Double) {
        if (index in list.indices) {
            list[index].progress = progress.toInt()
            notifyItemChanged(index, DownloadBroadcast.EXTRA_PROGRESS)
        }
    }

    fun updateState(state: String?, index: Int) {
        if (index in list.indices) {
            list[index].downloadState =
                if (state != null) DownloadState.valueOf(state) else DownloadState.IDLE
            notifyItemChanged(index, DownloadBroadcast.EXTRA_STATE)
        }
    }

}

enum class DownloadState {
    IDLE, QUEUED, DOWNLOADING, DOWNLOADED
}