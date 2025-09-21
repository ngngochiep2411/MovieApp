package com.example.movieapp.ui.download

import android.app.Dialog
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityPlayVideoDownloadBinding
import com.example.movieapp.databinding.FragmentListVideoDownloadBinding
import com.example.movieapp.model.Category
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.Film
import com.example.movieapp.model.ServerData
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
import java.io.File
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
    private lateinit var videoDownload: VideoDownload

    var list: ArrayList<ServerData> = ArrayList()
    private var thumb: String? = ""
    private var detailMovie: DetailMovie? = null
    private lateinit var adapter: ListVideoDownloadAdapter
    private var slug: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        slug = intent.getParcelableExtra<VideoDownload>("videoDownload")?.slug ?: ""
        thumb = intent.getParcelableExtra<VideoDownload>("videoDownload")?.thumb ?: ""
        if (intent.getParcelableExtra<VideoDownload>("videoDownload") != null) {
            val detailMovie = intent.getParcelableExtra<VideoDownload>("videoDownload")?.detailMovie
            this.detailMovie = detailMovie
            binding.tvMovieName.text = detailMovie?.movie?.name
            val info = "${detailMovie?.movie?.year} | ${detailMovie?.movie?.episodeTotal} tập | ${
                detailMovie?.movie?.country?.get(0)?.name
            } "
            binding.tvInfo.text = info
            val category = getCategory(detailMovie?.movie?.category)
            binding.tvCategory.text = category
        }
        initView()
        viewModel.getVideo(slug)
        viewModel.getVideoDownload(
            slug
        )
        observer()
    }


    private fun initView() {
        binding.playerView.layoutParams.height =
            Resources.getSystem().displayMetrics.heightPixels / 4
        binding.playerView.hideChooseTypeList()
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
        adapter = ListVideoDownloadAdapter(
            onItemClick = ::onItemClick,
            onDelete = ::onDelete
        )
        binding.recyclerView.adapter = adapter
        getFilm(slug = slug)
        binding.down.setOnClickListener {
            binding.info.visibility = View.VISIBLE
        }
    }

    private fun onItemClick(position: Int) {
        viewModel._position.value = position
        adapter.updateCurrentVideo(position)
    }

    private fun onDelete(position: Int) {
        showDialog(
            title = "Xóa nội dung đã tải?",
            message = "Bạn có muốn xóa nội dung đã tải xuống này không?",
            onAccept = {
                deleteFile(videoDownload.slug, position)
            })
    }

    private fun deleteFile(slug: String, position: Int) {
        viewModel.deleteFile(
            file = File(
                File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), slug),
                "Tập${position + 1}.mp4"
            ),
            position = position,
            onSuccess = { position ->
                adapter.list.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        )
    }

    private fun showDialog(
        title: String,
        message: String,
        onAccept: () -> Unit,
    ) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_dialog_confirm)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.findViewById<TextView>(R.id.cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.accept).setOnClickListener {
            dialog.dismiss()
            onAccept()
        }
        dialog.findViewById<TextView>(R.id.title).text = title
        dialog.findViewById<TextView>(R.id.message).text = message
        dialog.show()

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
            if (videos.isNotEmpty()) {
                playVideo(videos[viewModel.position.value ?: 0])
            }
        }
        viewModel.position.observe(this) {
            this.index = it
            if (videos.isNotEmpty()) {
                playVideo(videos[index])
            }
        }
        viewModel.videoDownload.observe(this) {
            this.videoDownload = it
            setData(videoDownload.detailMovie)
        }
    }

    private fun getFilm(slug: String?) {
        val list = ArrayList<Film>()
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            slug.toString()
        )

        val mp4Files = file.listFiles { f ->
            f.isFile && f.extension.equals("mp4", ignoreCase = true)
        } ?: emptyArray()


        mp4Files.forEachIndexed { index, f ->
            val film = Film(
                name = "",
                slug = slug.toString(),
                episode = f.name.split(".")[0],
                thumb = ""
            )
            list.add(film)
        }

        adapter.submitList(list, thumb, slug)

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
        updateView()
    }

    fun updateView() {
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