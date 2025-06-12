package com.example.movieapp.ui.detailmovie


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.databinding.ActivityDetailMovieBinding
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.util.SharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.LockClickListener
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailMovieActivity() : AppCompatActivity(), VideoAllCallBack {

    private lateinit var binding: ActivityDetailMovieBinding
    private var type: Int = -1
    private var movieName = ""
    private val viewModel: DetailMovieViewModel by viewModels()
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var detailMovie: DetailMovie
    private val sharedViewModel: SharedViewModel by viewModels()
    private var index = 0
    private var videos: List<String?> = ArrayList()

    var backupRendType: Int = 0
    var orientationUtils: OrientationUtils? = null
    var isPlay: Boolean = false
    var isPause: Boolean = false

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.main.setPadding(0, Utils.getStatusBarHeight(this), 0, 0)
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
                        detailMovie.episodes?.get(0)?.serverData!!,
                        detailMovie.movie?.thumbUrl,
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
        binding.playerView.setUp(url, true, "")
        binding.playerView.startPlayLogic()
    }


    private fun fetchData() {
        viewModel.getData(movieName)
    }

    private fun initView() {
        binding.playerView.layoutParams.height =
            Resources.getSystem().displayMetrics.heightPixels / 4
        backupRendType = GSYVideoType.getRenderType()
        resolveNormalVideoUI()
        initVideoBuilderMode()

        binding.playerView.setLockClickListener(object : LockClickListener {
            override fun onClick(view: View?, lock: Boolean) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils!!.setEnable(!lock)
                }
            }
        })
        //使用GL播放的话，用这种方式可以解决退出全屏黑屏的问题
        binding.playerView.setBackFromFullScreenListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                this@DetailMovieActivity.onBackPressed()
            }
        })

    }

    private fun initVideoBuilderMode() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, binding.playerView)
        //初始化不打开外部的旋转
        orientationUtils!!.setEnable(false)
        if (binding.playerView.fullscreenButton != null) {
            binding.playerView.fullscreenButton
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        showFull()
                    }
                })
        }
//        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL)
        GSYVideoOptionBuilder()
            .setCacheWithPlay(true)
            .setVideoTitle(" ")
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setSeekRatio(1F)
            .setVideoAllCallBack(this)
            .build(binding.playerView)

    }


    fun showFull() {
        if (orientationUtils!!.isLand != 1) {
            //直接横屏
            // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
            // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
            orientationUtils!!.resolveByClick()
        }
        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
        binding.playerView.startWindowFullscreen(
            this@DetailMovieActivity,
            true,
            true
        )
    }


    private fun resolveNormalVideoUI() {
        //增加title
        binding.playerView.titleTextView.visibility = View.GONE
        binding.playerView.backButton.setVisibility(View.GONE)
    }

    private fun updateCurrentVideo(position: Int) {
        sharedViewModel.changeVideoIndex(position)
    }

    override fun onStartPrepared(url: String?, vararg objects: Any?) {

    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
        if (orientationUtils == null) {
            throw NullPointerException("initVideo() or initVideoBuilderMode() first")
        }

        //开始播放了才能旋转和全屏
        orientationUtils?.setEnable(true && !isAutoFullWithSize())
        isPlay = true
    }

    @UnstableApi
    override fun onAutoComplete(url: String?, vararg objects: Any?) {
        if (index < videos.size - 1) {
            index++
            playVideo(videos[index])
            updateCurrentVideo(index)
        }
    }

    private fun isAutoFullWithSize(): Boolean = false

    override fun onClickStartIcon(url: String?, vararg objects: Any?) {

    }

    override fun onClickStartError(url: String?, vararg objects: Any?) {

    }

    override fun onClickStop(url: String?, vararg objects: Any?) {

    }

    override fun onClickStopFullscreen(url: String?, vararg objects: Any?) {

    }

    override fun onClickResume(url: String?, vararg objects: Any?) {

    }

    override fun onClickResumeFullscreen(url: String?, vararg objects: Any?) {

    }

    override fun onClickSeekbar(url: String?, vararg objects: Any?) {

    }

    override fun onClickSeekbarFullscreen(url: String?, vararg objects: Any?) {

    }


    override fun onComplete(url: String?, vararg objects: Any?) {

    }

    override fun onEnterFullscreen(url: String?, vararg objects: Any?) {

    }

    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {


        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
    }

    override fun onQuitSmallWidget(url: String?, vararg objects: Any?) {

    }

    override fun onEnterSmallWidget(url: String?, vararg objects: Any?) {

    }

    override fun onTouchScreenSeekVolume(url: String?, vararg objects: Any?) {

    }

    override fun onTouchScreenSeekPosition(url: String?, vararg objects: Any?) {

    }

    override fun onTouchScreenSeekLight(url: String?, vararg objects: Any?) {

    }

    override fun onPlayError(url: String?, vararg objects: Any?) {

    }

    override fun onClickStartThumb(url: String?, vararg objects: Any?) {

    }

    override fun onClickBlank(url: String?, vararg objects: Any?) {

    }

    override fun onClickBlankFullscreen(url: String?, vararg objects: Any?) {

    }

    override fun onBackPressed() {
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)

        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }


//    override fun onPause() {
//        super.onPause()
//        binding.playerView.getCurrentPlayer().onVideoPause()
//        if (orientationUtils != null) {
//            orientationUtils!!.setIsPause(true)
//        }
//        isPause = true
//    }

    override fun onResume() {
        super.onResume()
        binding.playerView.getCurrentPlayer().onVideoResume()
        if (orientationUtils != null) {
            orientationUtils!!.setIsPause(false)
        }
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            binding.playerView.getCurrentPlayer().release()
        }
        if (orientationUtils != null) orientationUtils!!.releaseListener()
    }

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            binding.playerView.onConfigurationChanged(
                this,
                newConfig,
                orientationUtils,
                true,
                true
            )
        }
    }
}