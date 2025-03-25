package com.example.movieapp.widgets

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerOnScrollListener(threshold: Int) : RecyclerView.OnScrollListener() {

    // The total number of items in the data set after the last load
    private var mPreviousTotal: Int = 0
    private var isLoading = false
    private var mFirstVisibleItem: Int = 0
    private var mVisibleItemCount: Int = 0
    private var mTotalItemCount: Int = 0
    private var mNumberThreshold: Int = -1
    private var mLastCompletelyVisibleItem: Int = 0

    init {
        mNumberThreshold = if (threshold >= 1) {
            threshold
        } else {
            1 // default num visible threshold
        }
    }

    override fun onScrolled(recycler: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recycler, dx, dy)
        mVisibleItemCount = recycler.childCount
        mTotalItemCount = recycler.layoutManager?.itemCount ?: 0
        mFirstVisibleItem = RecyclerAdapterUtils.getFirstVisibleItemPosition(recycler.layoutManager)
        mLastCompletelyVisibleItem =
            RecyclerAdapterUtils.getLastCompletelyVisibleItemPosition(recycler.layoutManager)

        if (isLoading) {
            stateLoading()
        }
        if (!isLoading) {
            if (mLastCompletelyVisibleItem >= (mTotalItemCount - mNumberThreshold)) {
                onLoadMore()
                isLoading = true
            } else if (mLastCompletelyVisibleItem < 0 && (mTotalItemCount - mVisibleItemCount) <= (mFirstVisibleItem + mNumberThreshold)) {
                onLoadMore()
                isLoading = true
            }
        }
    }

    private fun stateLoading() {
        if (mTotalItemCount > mPreviousTotal) {
            isLoading = false
            mPreviousTotal = mTotalItemCount
        }
    }

    fun resetOnLoadMore() {
        mFirstVisibleItem = 0
        mVisibleItemCount = 0
        mTotalItemCount = 0
        mPreviousTotal = 0
        isLoading = false
    }

    abstract fun onLoadMore()
}



object RecyclerAdapterUtils {
    @JvmStatic
    fun getFirstVisibleItemPosition(layoutManager: RecyclerView.LayoutManager?): Int {
        if (layoutManager is LinearLayoutManager) {
            return layoutManager.findFirstVisibleItemPosition()
        }
        if (layoutManager is GridLayoutManager) {
            return layoutManager.findFirstVisibleItemPosition()
        }
        if (layoutManager is StaggeredGridLayoutManager) {
            var firstVisibleItems: IntArray? = null
            firstVisibleItems = layoutManager.findFirstVisibleItemPositions(firstVisibleItems)
            if (firstVisibleItems != null && firstVisibleItems.isNotEmpty()) {
                return firstVisibleItems[0]
            }
        }
        return 0
    }

    @JvmStatic
    fun getLastCompletelyVisibleItemPosition(layoutManager: RecyclerView.LayoutManager?): Int {
        if (layoutManager is LinearLayoutManager) {
            return layoutManager.findLastCompletelyVisibleItemPosition()
        }
        if (layoutManager is GridLayoutManager) {
            return layoutManager.findLastVisibleItemPosition()
        }
        if (layoutManager is StaggeredGridLayoutManager) {
            var firstVisibleItems: IntArray? = null
            firstVisibleItems =
                layoutManager.findLastCompletelyVisibleItemPositions(firstVisibleItems)
            if (firstVisibleItems != null && firstVisibleItems.isNotEmpty()) {
                return firstVisibleItems[0]
            }
        }
        return 0
    }
}
