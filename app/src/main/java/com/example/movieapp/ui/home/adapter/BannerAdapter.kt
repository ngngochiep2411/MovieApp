package com.example.movieapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.LayoutItemBannerBinding
import com.example.movieapp.model.Banner
import com.example.movieapp.util.Utils

class BannerAdapter(
    val banners: List<Banner>,
    val itemClick: (banner: Banner) -> Unit,
) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(private val binding: LayoutItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            Glide.with(binding.root.context).load(banners[position].image).into(binding.image)
            binding.image.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Utils.getScreenHeight(binding.root.context) * 2 / 3
            )
            binding.name.text = banners[position].name
            binding.play.setOnClickListener {
                itemClick.invoke(banners[position])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BannerAdapter.BannerViewHolder {
        return BannerViewHolder(
            LayoutItemBannerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = banners.size

    override fun onBindViewHolder(holder: BannerAdapter.BannerViewHolder, position: Int) {
        holder.bind(position)
    }
}