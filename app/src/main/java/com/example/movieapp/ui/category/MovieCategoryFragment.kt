package com.example.movieapp.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.databinding.FragmentMovieCategoryBinding
import com.google.android.material.tabs.TabLayoutMediator


class MovieCategoryFragment : Fragment() {

    private lateinit var binding: FragmentMovieCategoryBinding
    private lateinit var viewPagerAdapter: ViewPagerSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()

    }

    private fun initViewPager() {
        binding.viewPager2.isUserInputEnabled = false
        binding.viewPager2.offscreenPageLimit = 7
        viewPagerAdapter = ViewPagerSearchAdapter(requireActivity())
        binding.viewPager2.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Phim Bộ"
                1 -> "Phim Lẻ"
                2 -> "TV Shows"
                3 -> "Hoạt Hình"
                4 -> "Phim việt sub"
                5 -> "Phim thuyết minh"
                6 -> "Phim lồng tiếng"
                else -> ""
            }
        }.attach()

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })


    }

    override fun onResume() {
        super.onResume()
    }
}