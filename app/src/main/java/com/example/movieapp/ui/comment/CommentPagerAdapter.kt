package com.example.movieapp.ui.comment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.ServerData
import com.example.movieapp.ui.comment.CommentFragment
import com.example.movieapp.ui.listvideo.LIstVideoFragment
import com.example.movieapp.ui.login.LoginFragment
import com.example.movieapp.ui.signin.SignInFragment

class CommentPagerAdapter(
    fragmentActivity: FragmentActivity,
    ) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment.newInstance()
            1 -> SignInFragment.newInstance()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }


}