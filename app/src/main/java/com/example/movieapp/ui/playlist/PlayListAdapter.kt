package com.example.movieapp.ui.playlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.model.PlayList
import androidx.core.graphics.toColorInt

class PlayListAdapter(
    private val onItemClick: (PlayList) -> Unit
) : ListAdapter<PlayList, PlayListAdapter.PlayListViewHolder>(DiffCallback) {

     var selectedPosition: Int = 0


    inner class PlayListViewHolder(itemView: View, val onItemClick: (PlayList) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.playListName)

        fun bind(item: PlayList) {
            tvName.text = item.playListName
            itemView.setOnClickListener {
                val oldPos = selectedPosition
                if (oldPos != bindingAdapterPosition) {
                    selectedPosition = bindingAdapterPosition
                    notifyItemChanged(bindingAdapterPosition)
                    notifyItemChanged(oldPos)
                    onItemClick(item)
                }

            }
            if (selectedPosition == bindingAdapterPosition) {
                itemView.setBackgroundResource(R.drawable.bg_playlist_selected)
                tvName.setTextColor(Color.BLACK)
            } else {
                itemView.setBackgroundResource(R.drawable.bg_playlist_normal)
                tvName.setTextColor("#72757e".toColorInt())
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_play_list, parent, false)
        return PlayListViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<PlayList>() {
        override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList): Boolean =
            oldItem == newItem
    }
}