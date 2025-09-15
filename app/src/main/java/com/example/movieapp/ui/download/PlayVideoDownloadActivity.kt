package com.example.movieapp.ui.download

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityPlayVideoDownloadBinding
import com.example.movieapp.model.Category
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.util.SharedViewModel
import com.example.movieapp.widgets.SampleControlVideo
import com.google.android.material.tabs.TabLayoutMediator
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.forEach

@AndroidEntryPoint
class PlayVideoDownloadActivity : AppCompatActivity(), VideoAllCallBack {

    private lateinit var binding: ActivityPlayVideoDownloadBinding
    private var videos: List<String> = emptyList()
    private val viewModel: PlayVideoDownloadViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    var backupRendType: Int = 0
    var orientationUtils: OrientationUtils? = null
    private var index = 0
    var isPlay: Boolean = false
    var isPause: Boolean = false
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var videoDownload: VideoDownload

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initViewPager2()
        val slug = intent.getParcelableExtra<VideoDownload>("videoDownload")?.slug ?: ""
        viewModel.getVideo(slug)
        viewModel.getVideoDownload(
            slug
        )
        observer()
    }

    private fun initViewPager2() {


        binding.viewPager2.isUserInputEnabled = true
        binding.viewPager2.offscreenPageLimit = 2
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager2.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Giới thiệu"
                1 -> "Các phim khác"
                else -> ""
            }
        }.attach()

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    private fun initView() {
        binding.playerView.layoutParams.height =
            Resources.getSystem().displayMetrics.heightPixels / 4
        binding.playerView.setSharedViewModel(sharedViewModel)
        backupRendType = GSYVideoType.getRenderType()
        resolveNormalVideoUI()
        initVideoBuilderMode()

        binding.playerView.setLockClickListener { view, lock ->
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils!!.setEnable(!lock)
            }
        }
        //使用GL播放的话，用这种方式可以解决退出全屏黑屏的问题
        binding.playerView.setBackFromFullScreenListener {
            this@PlayVideoDownloadActivity.onBackPressed()
        }


        binding.playerView.setVideoControlListener(object :
            SampleControlVideo.OnVideoControlListener {
            override fun onNextVideo() {
                nextVideo()
            }

            override fun onPreviousVideo() {
                previousVideo()
            }
        })

        binding.hideInfo.setOnClickListener {
            binding.info.visibility = View.GONE
        }
//        binding.playerView.setOnChangeType(object : OnChangeTypeClick {
//            override fun onChangeType(type: SharedViewModel.PlayType) {
//                typePlay = type
//                getVideos()
//                playVideo(videos[position])
//            }
//        })
    }

    private fun resolveNormalVideoUI() {
        //增加title
        binding.playerView.titleTextView.visibility = View.VISIBLE
        binding.playerView.backButton.visibility = View.VISIBLE
    }

    private fun initVideoBuilderMode() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, binding.playerView)
        //初始化不打开外部的旋转
        orientationUtils!!.setEnable(false)
        if (binding.playerView.fullscreenButton != null) {
            binding.playerView.fullscreenButton.setOnClickListener { showFull() }
        }
        GSYVideoOptionBuilder().setCacheWithPlay(true)
            .setVideoTitle(" ")
            .setIsTouchWiget(true)
            .setRotateViewAuto(false).setLockLand(false).setShowFullAnimation(false)
            .setNeedLockFull(true).setSeekRatio(1F).setVideoAllCallBack(this)
            .build(binding.playerView)

    }

    private fun observer() {
        viewModel.videos.observe(this) {
            videos = it
            playVideo(videos[viewModel.position.value ?: 0])
        }
        viewModel.position.observe(this) {
            this.index = it
            playVideo(videos[index])
        }
        viewModel.videoDownload.observe(this) {
            this.videoDownload = it
            viewPagerAdapter.submitList(
                videoDownload.detailMovie.episodes?.get(0)?.serverData!!,
                videoDownload.detailMovie.movie?.thumbUrl,
                videoDownload.detailMovie
            )
            setData(videoDownload.detailMovie)
        }
    }

    private fun setData(detailMovie: DetailMovie) {
        binding.name.text = detailMovie.movie?.name
        binding.content1.text = detailMovie.movie?.content
        binding.time.text = "Thời lượng : ${detailMovie.movie?.time}"
        binding.country.text = "Quốc gia : ${detailMovie.movie?.country[0]?.name}"
        binding.category.text = "Thể loại: ${getCategory(detailMovie.movie?.category)}"
        binding.episode.text = "Số tập : ${detailMovie.movie?.episodeTotal} tập"
        binding.year.text = "Năm phát hành : ${detailMovie.movie?.year}"
        binding.quality.text = "Quality : ${detailMovie.movie?.quality}"
        binding.type.text = "Lang: ${detailMovie.movie?.lang}"
        binding.actor.text = "${detailMovie.movie?.actor?.joinToString("\n") { "• $it" }}"
    }

    private fun getCategory(list: List<Category>?): String {
        var category = ""
        list?.forEach {
            category += "${it.name} | "
        }
        if (category.endsWith("| ")) {
            return category.substring(0, category.length - 3)
        }
        return category
    }

    fun nextVideo() {
        if (index < videos.size - 1) {
            viewModel.updatePosition(index++)
        }
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
            this@PlayVideoDownloadActivity, true, true
        )

    }

    fun previousVideo() {
        if (index > 0) {
            viewModel.updatePosition(index--)
        }
    }

    private fun playVideo(url: String?) {
        binding.playerView.setupURL(url)
    }

    override fun onPause() {
        super.onPause()
        binding.playerView.onVideoPause()
    }


    override fun onStartPrepared(url: String?, vararg objects: Any?) {

    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
        if (orientationUtils == null) {
            throw NullPointerException("initVideo() or initVideoBuilderMode() first")
        }
        orientationUtils?.setEnable(true && !isAutoFullWithSize())
        isPlay = true
    }

    @UnstableApi
    override fun onAutoComplete(url: String?, vararg objects: Any?) {
        nextVideo()
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
        orientationUtils?.backToProtVideo()
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
        Log.d("testing", "aaa")
        updateView()
    }

    fun updateView() {
        Log.d("testing", "${binding.playerView.startButton.isVisible}")
        if (binding.playerView.startButton.isVisible) {
            binding.playerView.findViewById<View>(R.id.next).visibility = View.INVISIBLE
            binding.playerView.findViewById<View>(R.id.previous).visibility = View.INVISIBLE
        } else {
            binding.playerView.findViewById<View>(R.id.next).visibility = View.VISIBLE
            binding.playerView.findViewById<View>(R.id.previous).visibility = View.VISIBLE
        }
    }

    override fun onClickBlankFullscreen(url: String?, vararg objects: Any?) {
        updateView()
    }

    override fun onBackPressed() {
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        orientationUtils?.backToProtVideo()
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
        orientationUtils?.setIsPause(false)
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            binding.playerView.getCurrentPlayer().release()
        }
        orientationUtils?.releaseListener()
    }

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            binding.playerView.onConfigurationChanged(
                this, newConfig, orientationUtils, true, true
            )
        }
    }

    fun showInfo() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.info.visibility = View.VISIBLE
        binding.info.startAnimation(animation)
    }

}