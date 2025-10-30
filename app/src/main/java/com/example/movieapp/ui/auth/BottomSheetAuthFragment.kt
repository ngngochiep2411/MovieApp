package com.example.movieapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.databinding.FragmentBottomSheetAuthBinding
import com.example.movieapp.ui.comment.CommentPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator


class BottomSheetAuthFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetAuthBinding
    lateinit var viewPagerAdapter: CommentPagerAdapter

    companion object {
        var TAG = BottomSheetAuthFragment::class.java.canonicalName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetAuthBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetDialog = dialog as BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.peekHeight = 800
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.viewPager.isUserInputEnabled = true
        viewPagerAdapter = CommentPagerAdapter(requireActivity())
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Đăng nhập"
                1 -> "Đăng kí"
                else -> ""
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }


    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }


    fun getViewPager(): ViewPager2 {
        return binding.viewPager
    }

}