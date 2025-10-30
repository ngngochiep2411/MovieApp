package com.example.movieapp.ui.listvideo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.LayoutPlayListBinding

class PlayListAdapter : ListAdapter<PlayList, PlayListAdapter.PlayListViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlayList>() {
            override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayListViewHolder {
        val binding = LayoutPlayListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlayListViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    class PlayListViewHolder(val binding: LayoutPlayListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playList: PlayList) {
            binding.name.text = playList.name
            binding.checkbox.isChecked = playList.isCheck
            binding.checkbox.setOnClickListener {
                playList.isCheck = !playList.isCheck
            }
        }
    }
}


data class PlayList(
    val playListId: Int,
    var isCheck: Boolean,
    val name: String
)