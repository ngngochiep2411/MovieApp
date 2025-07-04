package com.example.movieapp.ui.detailmovie

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.ServerData
import com.example.movieapp.ui.comment.CommentFragment
import com.example.movieapp.ui.listvideo.LIstVideoFragment

class ViewPagerAdapter(
    private val fragmentActivity: FragmentActivity,
    var list: ArrayList<ServerData> = ArrayList(),
    private var thumb: String? = "",
    private var detailMovie: DetailMovie? = null
) :
    FragmentStateAdapter(fragmentActivity) {
    fun submitList(list: ArrayList<ServerData>, thumbUrl: String?, detailMovie: DetailMovie) {
        this.list = list
        this.thumb = thumbUrl
        this.detailMovie = detailMovie
    }

    override fun getItemCount(): Int = 2

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val tag = "f" + holder.itemId
            val fragment = fragmentActivity.supportFragmentManager.findFragmentByTag(tag)
            if (fragment != null && (fragment is LIstVideoFragment || fragment is CommentFragment)) {
                when (fragment) {
                    is LIstVideoFragment -> {
                        fragment.updateList(list, thumb, detailMovie,detailMovie?.movie?.slug!!)
                    }
                    is CommentFragment -> {
                        fragment.updateVideoName(detailMovie?.movie?.slug)
                    }
                }

            } else {
                super.onBindViewHolder(holder, position, payloads)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LIstVideoFragment.newInstance(list, thumb, detailMovie)
            1 -> CommentFragment.newInstance(detailMovie?.movie?.slug)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }


}