package com.example.movieapp.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.movieapp.ui.category.MovieCategoryFragment
import com.example.movieapp.ui.home.HomeFragment
import com.example.movieapp.ui.profile.ProfileFragment

class MainAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> MovieCategoryFragment()
            2 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getItemCount(): Int = 3
}