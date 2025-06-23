package com.example.movieapp.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.animsh.animatedcheckbox.AnimatedCheckBox
import com.animsh.animatedcheckbox.AnimatedCheckBox.OnCheckedChangeListener
import com.example.movieapp.databinding.LayoutItemHistoryBinding
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.util.Utils


class HistoryAdapter(
    private var canDelete: Boolean = false,
    private var listCheck: ArrayList<Boolean>,
    private var lastCanDelete: Boolean = false
) : ListAdapter<MovieHistory, HistoryAdapter.HistoryViewHolder>(object :
    DiffUtil.ItemCallback<MovieHistory>() {
    override fun areItemsTheSame(
        oldItem: MovieHistory, newItem: MovieHistory
    ): Boolean {
        return oldItem.slug == newItem.slug
    }

    override fun areContentsTheSame(
        oldItem: MovieHistory, newItem: MovieHistory
    ): Boolean {
        return oldItem == newItem && lastCanDelete == canDelete
    }


}) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HistoryViewHolder(
            LayoutItemHistoryBinding.inflate(
                inflater, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: HistoryViewHolder, position: Int
    ) {
        holder.bind(position)
    }

    inner class HistoryViewHolder(private val binding: LayoutItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val movie = getItem(position) as MovieHistory
            Utils.loadImage(
                binding.root.context,
                movie.thumbUrl,
                binding.imgThumb
            )
            if (canDelete) {
                binding.rlCheckBox.visibility = View.VISIBLE
                binding.checkbox.isChecked = false
            } else {
                binding.rlCheckBox.visibility = View.GONE
                binding.checkbox.isChecked = false
            }

            binding.tvTotalTime.text = movie.total
            binding.movieName.text = movie.name
            binding.tvEpisode.text = "Đang xem tập ${(movie.episode) + 1}"

            val progression = (movie.watchedAt.toDouble() / movie.duration.toDouble()) * 100
            binding.tvWatchedAt.text = "Đã xem ${progression.toInt()}%"
            binding.progressBar.progress = progression.toFloat()

            binding.checkbox.isChecked = listCheck[position]
            binding.checkbox.setOnCheckedChangeListener(object : OnCheckedChangeListener {
                override fun onCheckedChanged(
                    checkBox: AnimatedCheckBox?,
                    isChecked: Boolean
                ) {
                    listCheck[position] = isChecked
                }

            })

            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(position)
            }
        }
    }


    fun setCanEdit(canDelete: Boolean) {
        lastCanDelete = canDelete
        this.canDelete = canDelete
    }


    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun resetListChecked() {
        for (i in listCheck.indices) {
            listCheck[i] = false
        }
    }
}