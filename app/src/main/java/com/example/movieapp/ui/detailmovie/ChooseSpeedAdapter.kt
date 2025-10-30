package com.example.movieapp.ui.detailmovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.LayoutItemChooseSpeedBinding

class ChooseSpeedAdapter(
    private val onSpeedSelected: (position: Int) -> Unit
) : ListAdapter<Speed, ChooseSpeedAdapter.ChooseSpeedViewHolder>(
    ChooseSpeedDiffUtil()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseSpeedViewHolder {
        return ChooseSpeedViewHolder(
            LayoutItemChooseSpeedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ChooseSpeedViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), onSpeedSelected)
    }

    class ChooseSpeedViewHolder(val binding: LayoutItemChooseSpeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(speed: Speed, onSpeedSelected: (position: Int) -> Unit) {
            binding.speedText.text = speed.value
            binding.tick.visibility = if (speed.isSelected) View.VISIBLE else View.INVISIBLE
            binding.root.setOnClickListener {
                onSpeedSelected(bindingAdapterPosition)
            }
        }

    }
}

class ChooseSpeedDiffUtil : DiffUtil.ItemCallback<Speed>() {
    override fun areItemsTheSame(oldItem: Speed, newItem: Speed): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Speed, newItem: Speed): Boolean {
        return oldItem == newItem
    }
}

data class Speed(
    var value: String,
    var isSelected: Boolean
)

