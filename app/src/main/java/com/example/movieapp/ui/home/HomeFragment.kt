package com.example.movieapp.ui.home

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.model.Banner
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.ui.home.adapter.HomeAdapter
import com.example.movieapp.ui.home.adapter.HomeAdapter.Companion.VIEW_TYPE_BANNER
import com.example.movieapp.ui.home.adapter.HomeAdapter.Companion.VIEW_TYPE_CARTOON
import com.example.movieapp.ui.home.adapter.HomeAdapter.Companion.VIEW_TYPE_MOVIE
import com.example.movieapp.ui.home.adapter.HomeAdapter.Companion.VIEW_TYPE_SERIES
import com.example.movieapp.ui.home.adapter.HomeAdapter.Companion.VIEW_TYPE_TV_SHOWS
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.ui.search.SearchActivity
import com.example.movieapp.util.NetworkResult
import com.example.movieapp.widgets.ToolbarHome
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener, ToolbarHome.OnItemClickListener {


    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private var homeAdapter = HomeAdapter(onBannerClick = ::bannerClick)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {

            } else {
            }
        }

    private var state = intArrayOf(0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        fetchData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {

                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    val dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.layout_dialog_request_per)
                    dialog.setCancelable(false)
                    dialog.findViewById<TextView>(R.id.cancel).setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.findViewById<TextView>(R.id.setting).setOnClickListener {
                        dialog.dismiss()
                        openNotificationSettings()
                    }
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.show()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    fun openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
            }
            startActivity(intent)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = ("package:${context?.packageName}").toUri()
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun initView() {
        homeAdapter.setOnItemClickListener(this)
        binding.rvList.adapter = homeAdapter
        binding.rvList.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                state[0] = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && (state[0] == 1 || state[0] == 2)) {
                    hideToolBar()
                } else if (dy < 0 && (state[0] == 1 || state[0] == 2)) {
                    showToolBar()
                }
            }
        })

        binding.refreshLayout.setOnRefreshListener {
            fetchData()
        }

        binding.toolBar.setOnItemClickListener(this)

    }

    private fun initObserver() {

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
//                binding.shimmerLayout.startShimmerAnimation()
//                binding.shimmerLayout.showSkeleton()
//                binding.shimmerLayout.visibility = View.VISIBLE

                binding.refreshLayout.isRefreshing = true
            } else {
//                binding.shimmerLayout.stopShimmerAnimation()
//                binding.shimmerLayout.showOriginal()
//                binding.shimmerLayout.visibility = View.GONE
                binding.refreshLayout.isRefreshing = false

            }
        }
        viewModel.movieResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Failure -> {
                }

                is NetworkResult.Success -> {
                    homeAdapter.submitList(it.data)
                    askNotificationPermission()
                }
            }
        }
    }

    private fun fetchData() {
        viewModel.getData()
    }

    private fun hideToolBar() {
        binding.toolBar.visibility = View.INVISIBLE
    }

    private fun showToolBar() {
        binding.toolBar.visibility = View.VISIBLE
    }

    override fun onItemClick(position: Int, type: Int?, name: String?) {

        when (type) {
            VIEW_TYPE_BANNER -> {
                name?.let {
                    openActivity(movieName = name, type = VIEW_TYPE_BANNER)
                }
            }

            VIEW_TYPE_MOVIE -> {
                name?.let {
                    openActivity(movieName = name, type = VIEW_TYPE_MOVIE)
                }
            }

            VIEW_TYPE_TV_SHOWS -> {
                name?.let {
                    openActivity(movieName = name, type = VIEW_TYPE_TV_SHOWS)
                }
            }

            VIEW_TYPE_CARTOON -> {
                name?.let {
                    openActivity(movieName = name, type = VIEW_TYPE_CARTOON)
                }
            }

            VIEW_TYPE_SERIES -> {
                name?.let {
                    openActivity(movieName = name, type = VIEW_TYPE_SERIES)
                }
            }
        }
    }

    private fun bannerClick(banner: Banner) {
        openActivity(movieName = banner.slug, type = VIEW_TYPE_BANNER)
    }

    private fun openActivity(movieName: String, type: Int) {
        val intent = Intent(context, DetailMovieActivity::class.java)
        intent.putExtra("name", movieName)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    override fun onSearchClick() {
        val intent = Intent(context, SearchActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
    }
}