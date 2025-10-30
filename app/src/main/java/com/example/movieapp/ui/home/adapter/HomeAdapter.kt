package com.example.movieapp.ui.home.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.LayoutBannerBinding
import com.example.movieapp.databinding.LayoutEmptyBinding
import com.example.movieapp.databinding.LayoutMovieBinding
import com.example.movieapp.databinding.LayoutMovieCartoonBinding
import com.example.movieapp.databinding.LayoutMovieSeriesBinding
import com.example.movieapp.databinding.LayoutMovieTvShowsBinding
import com.example.movieapp.model.Banner
import com.example.movieapp.model.CartoonMovie
import com.example.movieapp.model.HomeData
import com.example.movieapp.model.Movies
import com.example.movieapp.model.NewMovie
import com.example.movieapp.model.SeriesMovie
import com.example.movieapp.model.TvShowsMovie
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import com.zhpan.indicator.utils.IndicatorUtils

class HomeAdapter(
    val onBannerClick: (banner: Banner) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var homeData: HomeData? = null

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(homeData: HomeData) {
        this.homeData = homeData
        notifyDataSetChanged()
    }

    inner class MoviesViewHolder(private val binding: LayoutMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movies: Movies?) {
            val adapter = MovieAdapter(false)
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int, type: Int?, name: String?) {
                    onItemClickListener?.onItemClick(position, VIEW_TYPE_MOVIE, name)
                }

            })
            binding.recyclerView.adapter = adapter
            val list = movies?.data?.items?.filterNotNull()?.map { movie -> movie }
            adapter.submitList(list)

        }
    }

    inner class VietSubViewHolder(private val binding: LayoutMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movies: Movies?) {
            binding.text.text = movies?.data?.titlePage
            val adapter = MovieAdapter(false)
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int, type: Int?, name: String?) {
                    onItemClickListener?.onItemClick(position, VIEW_TYPE_MOVIE, name)
                }

            })
            binding.recyclerView.adapter = adapter
            val list = movies?.data?.items?.filterNotNull()?.map { movie -> movie }
            adapter.submitList(list)

        }
    }

    inner class ThuyetMinhViewHolder(private val binding: LayoutMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movies: Movies?) {
            binding.text.text = movies?.data?.titlePage
            val adapter = MovieAdapter(false)
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int, type: Int?, name: String?) {
                    onItemClickListener?.onItemClick(position, VIEW_TYPE_MOVIE, name)
                }

            })
            binding.recyclerView.adapter = adapter
            val list = movies?.data?.items?.filterNotNull()?.map { movie -> movie }
            adapter.submitList(list)

        }
    }

    inner class LongTiengViewHolder(private val binding: LayoutMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movies: Movies?) {
            binding.text.text = movies?.data?.titlePage
            val adapter = MovieAdapter(false)
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int, type: Int?, name: String?) {
                    onItemClickListener?.onItemClick(position, VIEW_TYPE_MOVIE, name)
                }

            })
            binding.recyclerView.adapter = adapter
            val list = movies?.data?.items?.filterNotNull()?.map { movie -> movie }
            adapter.submitList(list)

        }
    }

    inner class CartoonMovieViewHolder(private val binding: LayoutMovieCartoonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movies: CartoonMovie?) {
            val adapter = MovieAdapter(true)
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int, type: Int?, name: String?) {
                    onItemClickListener?.onItemClick(position, VIEW_TYPE_CARTOON, name)
                }
            })
            binding.recyclerView.adapter = adapter
            val list = movies?.data?.items?.filterNotNull()?.map { movie -> movie }
            adapter.submitList(list)
        }
    }

    inner class SeriesMovieViewHolder(private val binding: LayoutMovieSeriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movies: SeriesMovie?) {
            val adapter = MovieAdapter(true)
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int, type: Int?, name: String?) {
                    onItemClickListener?.onItemClick(position, VIEW_TYPE_SERIES, name)
                }
            })
            binding.recyclerView.adapter = adapter
            val list = movies?.data?.items?.filterNotNull()?.map { movie -> movie }
            adapter.submitList(list)
        }
    }

    inner class BannerViewHolder(private val binding: LayoutBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(newMovie: NewMovie?) {
            val images: List<Banner>? = newMovie?.items?.map {
                Banner(it.thumbUrl, it.name.toString(), it.slug.toString())
            }

            val adapter = images?.let {
                BannerAdapter(it) { banner ->
                    onBannerClick(banner)
                }
            }
            binding.viewPager.adapter = adapter
            binding.indicator.apply {
                setSliderColor(Color.parseColor("#feffff"), Color.parseColor("#5f6267"))
                setSliderGap(IndicatorUtils.dp2px(5f).toFloat())
                setSliderWidth(
                    resources.getDimension(R.dimen.dp5),
                    resources.getDimension(R.dimen.dp10)
                )
                setSliderHeight(resources.getDimension(R.dimen.dp5))
                setSlideMode(IndicatorSlideMode.NORMAL)
                setIndicatorStyle(IndicatorStyle.ROUND_RECT)
                setupWithViewPager(binding.viewPager)
            }
            if (images != null) {
                binding.viewPager.offscreenPageLimit = images.size
            }

        }
    }

    inner class EmptyViewHolder(private val binding: LayoutEmptyBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class TvShowsViewHolder(private val binding: LayoutMovieTvShowsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movies: TvShowsMovie?) {
            val adapter = MovieAdapter(true)
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int, type: Int?, name: String?) {
                    onItemClickListener?.onItemClick(position, VIEW_TYPE_TV_SHOWS, name)
                }
            })
            binding.recyclerView.adapter = adapter
            val list = movies?.data?.items?.filterNotNull()?.map { movie -> movie }
            adapter.submitList(list)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MOVIE -> {
                val binding =
                    LayoutMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MoviesViewHolder(binding)
            }

            VIEW_TYPE_VIET_SUB -> {
                val binding =
                    LayoutMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                VietSubViewHolder(binding)
            }


            VIEW_TYPE_THUYET_MINH -> {
                val binding =
                    LayoutMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ThuyetMinhViewHolder(binding)
            }


            VIEW_TYPE_LONG_TIENG -> {
                val binding =
                    LayoutMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LongTiengViewHolder(binding)
            }

            VIEW_TYPE_SERIES -> {
                val binding = LayoutMovieSeriesBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SeriesMovieViewHolder(binding)
            }

            VIEW_TYPE_CARTOON -> {
                val binding = LayoutMovieCartoonBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CartoonMovieViewHolder(binding)
            }

            VIEW_TYPE_TV_SHOWS -> {
                val binding = LayoutMovieTvShowsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TvShowsViewHolder(binding)
            }

            VIEW_TYPE_BANNER -> {
                val binding = LayoutBannerBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BannerViewHolder(binding)
            }

            else -> EmptyViewHolder(
                LayoutEmptyBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int = if (homeData != null) 8 else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MoviesViewHolder -> {
                holder.bind(homeData?.movies)
            }
            is VietSubViewHolder -> {
                holder.bind(homeData?.viet_sub)
            }

            is ThuyetMinhViewHolder -> {
                holder.bind(homeData?.thuyet_minh)
            }

            is LongTiengViewHolder -> {
                holder.bind(homeData?.long_tieng)
            }

            is TvShowsViewHolder -> {
                holder.bind(homeData?.tvShowsMovie)
            }

            is SeriesMovieViewHolder -> {
                holder.bind(homeData?.seriesMovie)
            }

            is CartoonMovieViewHolder -> {
                holder.bind(homeData?.cartoonMovie)
            }

            is BannerViewHolder -> {
                holder.bind(homeData?.newMovie)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> if (homeData?.newMovie != null) VIEW_TYPE_BANNER else VIEW_TYPE_EMPTY
            1 -> if (homeData?.movies != null) VIEW_TYPE_MOVIE else VIEW_TYPE_EMPTY
            2 -> if (homeData?.seriesMovie != null) VIEW_TYPE_SERIES else VIEW_TYPE_EMPTY
            3 -> if (homeData?.cartoonMovie != null) VIEW_TYPE_CARTOON else VIEW_TYPE_EMPTY
            4 -> if (homeData?.tvShowsMovie != null) VIEW_TYPE_TV_SHOWS else VIEW_TYPE_EMPTY
            5 -> if (homeData?.viet_sub != null) VIEW_TYPE_VIET_SUB else VIEW_TYPE_EMPTY
            6 -> if (homeData?.thuyet_minh != null) VIEW_TYPE_THUYET_MINH else VIEW_TYPE_EMPTY
            7 -> if (homeData?.long_tieng != null) VIEW_TYPE_LONG_TIENG else VIEW_TYPE_EMPTY
            else -> VIEW_TYPE_EMPTY
        }
    }


    companion object {
        const val VIEW_TYPE_BANNER = 0
        const val VIEW_TYPE_MOVIE = 1
        const val VIEW_TYPE_TV_SHOWS = 2
        const val VIEW_TYPE_CARTOON = 3
        const val VIEW_TYPE_SERIES = 4
        const val VIEW_TYPE_EMPTY = -1
        const val VIEW_TYPE_VIET_SUB = 5
        const val VIEW_TYPE_THUYET_MINH = 6
        const val VIEW_TYPE_LONG_TIENG = 7
    }
}