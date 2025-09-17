package com.example.movieapp.widgets

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.LayoutItemFilterBinding

class FilterAdapter(
    val list: List<String>, var currentIndex: Int , val onClick: (String, Int) -> Unit
) : ListAdapter<String, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class FilterViewHolder(val binding: LayoutItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(position: Int) {
            binding.text.text = list[position]
            if (currentIndex == position) {
                binding.text.background.setTint(binding.root.context.getColor(R.color.textGray2))
                binding.text.setTextColor(binding.root.context.getColor(R.color.primaryColor))
            } else {
                binding.text.background.setTint(Color.parseColor("#00000000"))
                binding.text.setTextColor(binding.root.context.getColor(R.color.white))
            }


            binding.root.setOnClickListener {
                currentIndex = if (currentIndex == position) -1 else position
                onClick(if (currentIndex == position) list[position] else "", currentIndex)
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FilterViewHolder(
            LayoutItemFilterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FilterViewHolder).bind(position)
    }

}





