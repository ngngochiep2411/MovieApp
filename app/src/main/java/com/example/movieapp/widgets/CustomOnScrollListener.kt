package com.example.movieapp.widgets


import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2


class CustomOnScrollListener(private val viewPager: ViewPager2) : RecyclerView.OnScrollListener() {


    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!recyclerView.canScrollHorizontally(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (viewPager.currentItem < 3) {
                viewPager.isUserInputEnabled = true
                viewPager.post {

                }
                Handler().post {
                    viewPager.setCurrentItem(viewPager.currentItem + 1, true)
                }

            }
        }
//        if (!recyclerView.canScrollHorizontally(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
//            if (viewPager.currentItem < 3) {
//                viewPager.setCurrentItem(viewPager.currentItem - 1, true)
//            }
//        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        viewPager.isUserInputEnabled = false
    }
}