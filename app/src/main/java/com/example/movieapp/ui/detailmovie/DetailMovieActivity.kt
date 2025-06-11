package com.example.movieapp.ui.detailmovie


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.databinding.ActivityDetailMovieBinding
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.util.SharedViewModel
import com.example.movieapp.util.Utils
import com.google.android.material.tabs.TabLayoutMediator
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailMovieActivity : AppCompatActivity(), Player.Listener {

    private lateinit var binding: ActivityDetailMovieBinding
    private var type: Int = -1
    private var movieName = ""
    private val viewModel: DetailMovieViewModel by viewModels()
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var detailMovie: DetailMovie
    private val sharedViewModel: SharedViewModel by viewModels()
    private var index = 0
    private var videos: List<String?> = ArrayList()
    var orientationUtils: OrientationUtils? = null

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.setPadding(0, Utils.getStatusBarHeight(this), 0, 0)
        movieName = intent.getStringExtra("name").toString()
        type = intent.getIntExtra("type", -1)
        fetchData()
        initView()
        setOnClick()
        initObserver()
        initViewPager()
    }

    private fun initViewPager() {
        binding.viewPager2.isUserInputEnabled = true
        binding.viewPager2.offscreenPageLimit = 1
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager2.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Giới thiệu"
                1 -> "Bình luận"
                else -> ""
            }
        }.attach()

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })


    }

    private fun setOnClick() {
        binding.playerView.fullscreenButton.setOnClickListener {
            Log.d("testing","hehe")
            //直接横屏
            orientationUtils?.resolveByClick();
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            binding.playerView.startWindowFullscreen(this, true, true);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    @UnstableApi
    private fun initObserver() {
        lifecycleScope.launch {
            launch {
                viewModel.videoUrls.filterNotNull().collect { urls ->
                    videos = urls
                    Log.d("testing", "$videos")
                    updateCurrentVideo(0)
                }
            }
            launch {
                viewModel.detailMovie.filterNotNull().collect { data ->
                    detailMovie = data
                    viewPagerAdapter.submitList(
                        detailMovie.episodes?.get(0)?.serverData!!, detailMovie.movie?.thumbUrl,
                        detailMovie
                    )
                    viewPagerAdapter.notifyItemChanged(0, "change")
                    viewPagerAdapter.notifyItemChanged(1, "change")
                }
            }
            launch {
                sharedViewModel.videoIndex.collect { current ->
                    if (current != -1) {
                        index = current
                        playVideo(videos[index])
                    }
                }
            }
        }
    }


    @UnstableApi
    private fun playVideo(url: String?) {
//        val gsyVideoOptionBuilder = GSYVideoOptionBuilder()
//
//        gsyVideoOptionBuilder
//            .setIsTouchWiget(true)
//            .setRotateViewAuto(false)
//            .setLockLand(false)
//            .setAutoFullWithSize(true)
//            .setShowFullAnimation(false)
//            .setNeedLockFull(true)
//            .setUrl(url)
//            .setCacheWithPlay(false)
//            .setVideoTitle("测试视频")
//            .build(binding.playerView)
//
//        binding.playerView.startPlayLogic()
    }


    private fun fetchData() {
        viewModel.getData(movieName)
    }

    private fun initView() {
        orientationUtils = OrientationUtils(this, binding.playerView)
        orientationUtils?.setEnable(false)
        binding.playerView.layoutParams.height =
            Resources.getSystem().displayMetrics.heightPixels / 4

    }

    private fun updateCurrentVideo(position: Int) {
        sharedViewModel.changeVideoIndex(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }
    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }
    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            binding.playerView.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
        }
    }
}