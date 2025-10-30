package com.example.movieapp.ui.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.movieapp.databinding.FragmentMovieSearchResultBinding
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.model.MultiViewItem
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.widgets.CustomItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import androidx.core.view.isInvisible


@AndroidEntryPoint
class MovieSearchResultFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentMovieSearchResultBinding
    private var type: Int? = -1
    private val viewModel: MovieSearchResultViewModel by viewModels()
    private lateinit var adapter: SearchAdapter
    var category: String = ""
    var country: String = ""
    var year: String = ""
    var name = ""
    private val header = mutableListOf<MultiViewItem?>(
        MultiViewItem.CategoryHeader(),
        MultiViewItem.CountryHeader(),
        MultiViewItem.YearHeader()
    )


    companion object {
        fun newInstance(type: Int): MovieSearchResultFragment {
            val fragment = MovieSearchResultFragment()
            val bundle = Bundle()
            bundle.putInt("type", type)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = arguments?.getInt("type", -1)
        fetchData()

        when (type) {
            0 -> {
                name = "Phim bộ"
            }

            1 -> {
                name = "Phim lẻ"
            }

            2 -> {
                name = "TV Shows"
            }

            3 -> {
                name = "Hoạt hình"
            }

            4 -> {
                name = "Phim việt sub"
            }

            5 -> {
                name = "Phim thuyết minh"
            }

            6 -> {
                name = "Phim lồng tiếng"
            }
        }

        initView()
        initObserver()
    }

    private fun fetchData() {
        viewModel.getData(type, category, country, year)
    }

    private fun filterData() {
        viewModel.isLoading.value = true
        lifecycleScope.launch {
            viewModel.getMovie(type, category, country, year).collect {
                val header = adapter.currentList.take(3)
                val newList = mutableListOf<MultiViewItem?>().apply {
                    addAll(header)
                    addAll(it)
                    if (viewModel.nextPage) {
                        add(MultiViewItem.LoadingMore())
                    }
                }
                adapter.submitList(newList)
            }
        }

    }

    private fun loadMoreData() {
        viewModel.getMoreData(type, category, country, year)
    }


    private fun initView() {
        when (type) {
            0, 4, 5, 6 -> {
                adapter = SearchAdapter(
                    isSeriesMovie = true, setWidth = false,
                    loadMoreData = {
                        loadMoreData()
                    },
                    onCountryClick = {
                        country = it
                        filterData()
                    },
                    onYearClick = {
                        year = it
                        filterData()
                    },
                    onCategoryClick = {
                        category = it
                        filterData()
                    },
                )

            }

            1 -> {
                adapter = SearchAdapter(
                    isSeriesMovie = false, setWidth = false,
                    loadMoreData = {
                        loadMoreData()
                    },
                    onCountryClick = {
                        country = it
                        filterData()
                    },
                    onYearClick = {
                        year = it
                        filterData()
                    },
                    onCategoryClick = {
                        category = it
                        filterData()
                    },
                )
            }

            2 -> {
                adapter = SearchAdapter(
                    isSeriesMovie = true, setWidth = false,
                    loadMoreData = {
                        loadMoreData()
                    },
                    onCountryClick = {
                        country = it
                        filterData()
                    },
                    onYearClick = {
                        year = it
                        filterData()
                    },
                    onCategoryClick = {
                        category = it
                        filterData()
                    },
                )
            }

            3 -> {
                adapter = SearchAdapter(
                    isSeriesMovie = true, setWidth = false,
                    loadMoreData = {
                        loadMoreData()
                    },
                    onCountryClick = {
                        country = it
                        filterData()
                    },
                    onYearClick = {
                        year = it
                        filterData()
                    },
                    onCategoryClick = {
                        category = it
                        filterData()
                    },
                )
            }

        }
        adapter.setOnItemClickListener(this)
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener {
            fetchData()
        }


        (binding.recyclerView.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position == 0 || position == 1 || position == 2 ||
                        adapter.currentList[position] is MultiViewItem.LoadingMore
                    ) {
                        return 3
                    }
                    return 1
                }

            }

        binding.recyclerView.addItemDecoration(CustomItemDecoration(3))

        binding.recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val text =
                    " $name • ${category.ifBlank { "Chưa chọn thể loại" }} • ${country.ifBlank { "Chưa chọn quốc gia" }} • ${year.ifBlank { "Chưa chọn năm" }}"
                binding.textFilter.text = text

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition == 3) {
                    showFilter()
                }
                if (firstVisibleItemPosition == 0) {
                    hideFilter()
                }
            }
        })
        binding.textFilter.setOnClickListener {
            binding.recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun showFilter() {
        if (binding.textFilter.isInvisible) binding.textFilter.visibility =
            View.VISIBLE
    }

    private fun hideFilter() {
        if (binding.textFilter.isVisible) binding.textFilter.visibility =
            View.INVISIBLE
    }


    private fun initObserver() {
        lifecycleScope.launch {

            launch {
                viewModel.moreMovie.collect {
                    if (!it.isNullOrEmpty()) {
                        val newList = adapter.currentList.toMutableList().apply {
                            removeAt(adapter.currentList.size - 1)
                            addAll(it)
                            if (viewModel.nextPage) {
                                add(MultiViewItem.LoadingMore())
                            }
                        }
                        adapter.submitList(newList)
                    }

                }
            }

            launch {
                viewModel.movie.collect {
                    if (it != null) {
                        val newList = mutableListOf<MultiViewItem?>()
                        (header[0] as MultiViewItem.CategoryHeader).list = it.category
                        (header[1] as MultiViewItem.CountryHeader).list = it.country
                        newList.addAll(header)
                        newList.addAll(it.movies)
                        if (viewModel.nextPage) {
                            newList.add(MultiViewItem.LoadingMore())
                        }
                        adapter.submitList(newList)
                    }
                }
            }

            launch {
                viewModel.isLoading.observe(viewLifecycleOwner) {
                    binding.refreshLayout.isRefreshing = it
                }
            }


        }
    }


    override fun onItemClick(position: Int, type: Int?, name: String?) {
        openActivity(movieName = name!!, type = 0)
    }

    private fun openActivity(movieName: String, type: Int) {
        val intent = Intent(context, DetailMovieActivity::class.java)
        intent.putExtra("name", movieName)
        intent.putExtra("type", type)
        startActivity(intent)
    }
}


