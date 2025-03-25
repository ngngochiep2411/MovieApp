package com.example.movieapp.ui.detailmovie


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityDetailMovieBinding
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.util.SharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailMovieActivity : AppCompatActivity(), Player.Listener {

    private var isFullScreen: Boolean = false
    private lateinit var binding: ActivityDetailMovieBinding
    private lateinit var player: ExoPlayer
    private var type: Int = -1
    private var movieName = ""
    private val viewModel: DetailMovieViewModel by viewModels()
    private lateinit var btnPlayPause: ImageButton
    private lateinit var imgBack: ImageButton
    private lateinit var btnRew: ImageButton
    private lateinit var btnFfwd: ImageButton
    private lateinit var btnPre: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnFullScreen: ImageButton
    private lateinit var progress: SeekBar
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var detailMovie: DetailMovie
    private val sharedViewModel: SharedViewModel by viewModels()
    private var index = 0
    private var videos: List<String?> = ArrayList()

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        btnPlayPause.setOnClickListener {
            if (player.isPlaying) {
                pause()
            } else {
                play()
            }
        }

        btnFfwd.setOnClickListener {
            seekTo(player.currentPosition + 10000)
        }
        btnRew.setOnClickListener {
            seekTo(player.currentPosition - 10000)
        }
        btnNext.setOnClickListener {
            incrementVideo()
        }
        btnPre.setOnClickListener {
            decrementVideo()
        }
        btnFullScreen.setOnClickListener {
            toggleFullScreen()
        }

        imgBack.setOnClickListener {
            if (!isPlayVideoFullScreen()) {
                this.finish()
            } else {
                setOrientationPortrait()
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    @UnstableApi
    private fun initObserver() {
        lifecycleScope.launch {
            val job1 = launch {
                viewModel.videoUrls.filterNotNull().collect { urls ->
                    videos = urls
                    updateCurrentVideo(0)
                }
            }
            val job2 = launch {
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
//                        playVideo(videos[index])
                        btnNext.alpha = if (canPlayNextVideo()) 1F else 0.5F
                        btnPre.alpha = if (canPlayPreVideo()) 1F else 0.5F
                    }
                }
            }
        }
    }

    private fun canPlayNextVideo(): Boolean {
        return videos.size > 1 && (index >= 0 && index < videos.size - 1)
    }

    private fun canPlayPreVideo(): Boolean {
        return videos.size > 1 && (index > 0)
    }


    @UnstableApi
    private fun playVideo(url: String?) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSource: HlsMediaSource =
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url!!))

        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()
    }


    private fun fetchData() {
        viewModel.getData(movieName)
    }

    private fun initView() {
        binding.playerView.layoutParams.height =
            Resources.getSystem().displayMetrics.heightPixels / 4
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
        btnPlayPause = binding.playerView.findViewById(R.id.img_play_pause)
        imgBack = binding.playerView.findViewById(R.id.imgBack)
        btnRew = binding.playerView.findViewById(R.id.img_rew)
        btnFfwd = binding.playerView.findViewById(R.id.img_ffwd)
        btnPre = binding.playerView.findViewById(R.id.img_pre)
        btnNext = binding.playerView.findViewById(R.id.img_next)
        progress = binding.playerView.findViewById(R.id.progress)
        btnFullScreen = binding.playerView.findViewById(R.id.imgFullScreen)
        player.addListener(this)

        progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) seekTo(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    private fun setDuration() {


    }

    private fun seekTo(position: Long) {
        player.seekTo(position)
    }

    private fun play() {
        btnPlayPause.setImageResource(R.drawable.ic_pause)
        player.play()
    }

    private fun pause() {
        btnPlayPause.setImageResource(R.drawable.ic_play)
        player.pause()
    }

    private fun setOrientationLandScape() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        isFullScreen = true


        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        val layoutParams = binding.playerView.layoutParams as LinearLayout.LayoutParams
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
        binding.playerView.layoutParams = layoutParams
    }

    private fun isPlayVideoFullScreen(): Boolean {
        return (binding.playerView.layoutParams.width == LinearLayout.LayoutParams.MATCH_PARENT && binding.playerView.layoutParams.height == LinearLayout.LayoutParams.MATCH_PARENT)
    }

    private fun setOrientationPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        isFullScreen = false


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        val layoutParams = binding.playerView.layoutParams as LinearLayout.LayoutParams
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = resources.getDimension(R.dimen.dp200).toInt()
        binding.playerView.layoutParams = layoutParams
    }

    private fun toggleFullScreen() {
        if (isFullScreen) {
            setOrientationPortrait()
        } else {
            setOrientationLandScape()
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        updateSeekBar()
    }

    private fun updateSeekBar() {

    }

    var handler: Handler = Handler(Looper.getMainLooper())
    override fun onPlaybackStateChanged(@Player.State state: Int) {
        when (state) {
            ExoPlayer.STATE_BUFFERING -> {
            }

            ExoPlayer.STATE_READY -> {
                progress.max = player.duration.toInt()
                val runnable: Runnable = object : Runnable {
                    override fun run() {
                        if (player.isPlaying) {
                            val currentPosition = player.currentPosition
                            progress.progress = currentPosition.toInt()
                            handler.postDelayed(this, 1000)
                        }
                    }
                }
                handler.post(runnable)

            }

            ExoPlayer.STATE_ENDED -> {
                incrementVideo()
            }

            else -> {
            }
        }
    }

    private fun incrementVideo() {
        progress.max = 0
        progress.progress = 0
        if (videos.size > 1 && index < videos.size - 1) {
            index++
            updateCurrentVideo(index)
        }
    }

    private fun decrementVideo() {
        if (videos.size > 1 && index >= 0) {
            index--
            updateCurrentVideo(index)
        }
    }


    private fun updateCurrentVideo(position: Int) {
        sharedViewModel.changeVideoIndex(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

}