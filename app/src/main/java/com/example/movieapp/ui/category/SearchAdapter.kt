package com.example.movieapp.ui.category


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.LayoutItemMovieSearchBinding
import com.example.movieapp.databinding.LayoutItemRvBinding
import com.example.movieapp.databinding.LayoutLoadingBinding
import com.example.movieapp.model.CategoryMovie
import com.example.movieapp.model.CountryMovie
import com.example.movieapp.model.Movie
import com.example.movieapp.model.MultiViewItem
import com.example.movieapp.model.MultiViewItem.YearHeader.Companion.yearsList
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.util.Utils
import com.example.movieapp.widgets.FilterAdapter


class SearchAdapter(
    private val isSeriesMovie: Boolean,
    private val setWidth: Boolean = true,
    private val loadMoreData: () -> Unit = {},
    val onCategoryClick: (String) -> Unit,
    val onCountryClick: (String) -> Unit,
    val onYearClick: (String) -> Unit,
) : ListAdapter<MultiViewItem, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<MultiViewItem>() {
    override fun areItemsTheSame(oldItem: MultiViewItem, newItem: MultiViewItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: MultiViewItem, newItem: MultiViewItem): Boolean {
        return oldItem == newItem
    }

}) {


    private val TYPE_LOADING = -1
    private val TYPE_FILTER_CATEGORY = 0
    private val TYPE_FILTER_COUNTRY = 1
    private val TYPE_YEAR = 2
    private val TYPE_LIST_MOVIE = 3


    inner class CategoryViewHolder(private val binding: LayoutItemRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(multiViewItem: MultiViewItem) {
            val list = (multiViewItem as MultiViewItem.CategoryHeader).list.map { it.name }
            val adapter = FilterAdapter(
                list,
                multiViewItem.index
            ) { string, index ->
                onCategoryClick(string)
                multiViewItem.index = index
            }
            binding.recyclerView.adapter = adapter
        }

    }

    inner class CountryViewHolder(private val binding: LayoutItemRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(multiViewItem: MultiViewItem) {
            val list = (multiViewItem as MultiViewItem.CountryHeader).list.map { it.name }
            val adapter = FilterAdapter(
                list,
                multiViewItem.index
            ) { string, index ->
                onCountryClick(string)
                multiViewItem.index = index
            }
            binding.recyclerView.adapter = adapter
        }
    }

    inner class YearViewHolder(private val binding: LayoutItemRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(multiViewItem: MultiViewItem) {
            val adapter = FilterAdapter(
                yearsList,
                (multiViewItem as MultiViewItem.YearHeader).index
            ) { string, index ->
                onYearClick(string)
                multiViewItem.index = index
            }
            binding.recyclerView.adapter = adapter
        }
    }

    inner class LoadingViewHolder(binding: LayoutLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            loadMoreData.invoke()
        }
    }


    inner class MovieViewHolder(private val binding: LayoutItemMovieSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {

            Utils.loadImage(
                binding.root.context,
                "https://phimimg.com/" + movie.thumbUrl,
                binding.imgThumb
            )
            binding.movieName.text = movie.name
            if (isSeriesMovie) {
                binding.tvEpisodeCurrent.visibility = View.VISIBLE
                binding.tvTotalTime.visibility = View.INVISIBLE
                binding.tvEpisodeCurrent.text = movie.episodeCurrent
            } else {
                binding.tvTotalTime.visibility = View.VISIBLE
                binding.tvEpisodeCurrent.visibility = View.INVISIBLE
                binding.tvTotalTime.text = movie.time
            }

            if (setWidth) {
                binding.root.layoutParams.width =
                    (Utils.getScreenWidth(binding.root.context) / (2.5) - 20).toInt()
            }

            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(position = adapterPosition, name = movie.slug)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_YEAR -> {
                YearViewHolder(
                    LayoutItemRvBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            TYPE_FILTER_CATEGORY -> {
                CategoryViewHolder(
                    LayoutItemRvBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            TYPE_FILTER_COUNTRY -> {
                CountryViewHolder(
                    LayoutItemRvBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            TYPE_LIST_MOVIE -> {
                MovieViewHolder(
                    LayoutItemMovieSearchBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            TYPE_LOADING -> {
                LoadingViewHolder(
                    LayoutLoadingBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            else -> {
                throw IllegalArgumentException("Invalid view type: $viewType")
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Movie -> (holder as MovieViewHolder).bind(item)
            is MultiViewItem.CategoryHeader -> (holder as CategoryViewHolder).bind(item)
            is MultiViewItem.CountryHeader -> (holder as CountryViewHolder).bind(item)
            is MultiViewItem.YearHeader -> (holder as YearViewHolder).bind(item)
            is MultiViewItem.LoadingMore -> (holder as LoadingViewHolder).bind()
            else -> throw IllegalArgumentException("Unknown view type at position $position")
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MultiViewItem.CategoryHeader -> TYPE_FILTER_CATEGORY
            is MultiViewItem.CountryHeader -> TYPE_FILTER_COUNTRY
            is MultiViewItem.YearHeader -> TYPE_YEAR
            is MultiViewItem.LoadingMore -> TYPE_LOADING
            is Movie -> TYPE_LIST_MOVIE
            else -> throw IllegalArgumentException("Unknown view type at position $position")
        }
    }
}