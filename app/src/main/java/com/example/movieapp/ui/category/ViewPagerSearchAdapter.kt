package com.example.movieapp.ui.category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class ViewPagerSearchAdapter(
    fragmentActivity: FragmentActivity,
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 7


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MovieSearchResultFragment.newInstance(0)
            1 -> MovieSearchResultFragment.newInstance(1)
            2 -> MovieSearchResultFragment.newInstance(2)
            3 -> MovieSearchResultFragment.newInstance(3)
            4 -> MovieSearchResultFragment.newInstance(4)
            5 -> MovieSearchResultFragment.newInstance(5)
            6 -> MovieSearchResultFragment.newInstance(6)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}