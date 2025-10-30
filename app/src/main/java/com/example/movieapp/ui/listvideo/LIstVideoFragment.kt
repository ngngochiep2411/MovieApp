package com.example.movieapp.ui.listvideo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentLIstVideoBinding
import com.example.movieapp.model.Category
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.ServerData
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.service.DownloadService
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.ui.detailmovie.Speed
import com.example.movieapp.ui.listvideo.adapter.ListVideoAdapter
import com.example.movieapp.ui.listvideo.adapter.PlayList
import com.example.movieapp.ui.listvideo.adapter.PlayListAdapter
import com.example.movieapp.util.SendBroadCast.Companion.sendBroadCast
import com.example.movieapp.util.SendBroadCast.Companion.startService
import com.example.movieapp.util.SharedViewModel
import com.example.movieapp.util.VideoDownloader
import com.example.movieapp.widgets.SampleControlVideo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class LIstVideoFragment : Fragment() {

    private lateinit var binding: FragmentLIstVideoBinding
    private lateinit var adapter: ListVideoAdapter
    var list: ArrayList<ServerData> = ArrayList()
    private var thumb: String? = ""
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: ListVideoViewModel by viewModels()
    private var detailMovie: DetailMovie? = null
    private var slug: String? = ""
    private lateinit var videoDownloader: VideoDownloader

    private var downloadService: DownloadService? = null
    private var downloadModes = listOf<Speed>()

    object DownloadMode {
        const val VIETSUB = "VietSub"
        const val THUYET_MINH = "ThuyetMinh"
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadService = (service as DownloadService.LocalBinder).getService()
            val currentQueue = downloadService?.getQueue()
            currentQueue?.forEach { task ->
                adapter.list.getOrNull(task.position)?.apply {
                    downloadState = DownloadService.DownloadState.DOWNLOADING
                }
            }
            adapter.notifyDataSetChanged()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            downloadService = null
        }
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {
                DownloadService.ACTION_UPDATE_PROGRESS -> {

                    val index = intent.getIntExtra(DownloadService.EXTRA_INDEX, -1)
                    val progress = intent.getDoubleExtra(DownloadService.EXTRA_PROGRESS, 0.0)
                    val slug = intent.getStringExtra(DownloadService.EXTRA_SLUG)
                    if (slug != null && slug == this@LIstVideoFragment.slug) {
                        if (index >= 0) {
                            adapter.updateProgress(
                                index, progress, DownloadService.DownloadState.DOWNLOADING.name
                            )
                        }
                    }

                }

                DownloadService.ACTION_UPDATE_STATE -> {
                    val slug = intent.getStringExtra(DownloadService.EXTRA_SLUG)
                    val state = intent.getStringExtra(DownloadService.EXTRA_STATE)
                    val index = intent.getIntExtra(DownloadService.EXTRA_INDEX, -1)
                    if (slug != null && slug == this@LIstVideoFragment.slug) {
                        adapter.updateState(state, index)
                    }

                }
            }
        }
    }

    companion object {
        fun newInstance(
        ): LIstVideoFragment {
            val fragment = LIstVideoFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLIstVideoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoDownloader = VideoDownloader(requireContext())
        adapter = ListVideoAdapter(
            list = list,
            thumb = thumb,
            slug = this.slug,
            onItemClick = { position ->
                updateCurrentVideo(position, slug)
                updateEpisode(slug, position)
                updateWatchedAt()
            },
            onDownloadClick = { position ->
                saveVideoDownload()
                val state = adapter.list[position].downloadState
                when (state) {
                    DownloadService.DownloadState.DOWNLOADED -> {
                        showDialog(
                            title = "Xóa nội dung đã tải?",
                            message = "Nội dung này sẽ không có sẵn trên thiết bị để xem khi không có Internet.",
                            onAccept = {
                                deleteFile(
                                    position, onSuccess = {
                                        sendBroadCast(
                                            context = requireContext(),
                                            action = DownloadService.ACTION_UPDATE_STATE,
                                            position = position,
                                            state = DownloadService.DownloadState.IDLE.name,
                                            slug = slug
                                        )
                                        startService(
                                            context = requireContext(),
                                            act = DownloadService.ACTION_REMOVE_QUEUE,
                                            url = list[position].linkM3u8,
                                            slug = this.slug,
                                            movieName = detailMovie?.movie?.name,
                                            position = position
                                        )
                                    })
                            })
                    }

                    DownloadService.DownloadState.QUEUED -> {
                        showDialog(
                            title = "Hủy tải xuống?",
                            message = "Nội dung này đang được chờ để được tải xuống. Bạn có muốn hủy bỏ việc tải xuống nội dung này?",
                            onAccept = {
                                sendBroadCast(
                                    context = requireContext(),
                                    action = DownloadService.ACTION_UPDATE_STATE,
                                    position = position,
                                    state = DownloadService.DownloadState.IDLE.name,
                                    slug = slug
                                )
                                startService(
                                    context = requireContext(),
                                    act = DownloadService.ACTION_REMOVE_QUEUE,
                                    url = list[position].linkM3u8,
                                    slug = this.slug,
                                    movieName = detailMovie?.movie?.name,
                                    position = position
                                )
                            })
                    }

                    DownloadService.DownloadState.IDLE -> {
                        if (getPlayerMode() > 1) {
                            showChooseTypeDownloadDialog(
                                onAccept = { downloadMode ->
                                    startService(
                                        context = requireContext(),
                                        act = DownloadService.ACTION_START,
                                        url = getUrlDownload(
                                            type = downloadMode,
                                            position = position
                                        ),
                                        slug = this.slug,
                                        movieName = detailMovie?.movie?.name,
                                        position = position,
                                        downloadMode = downloadMode

                                    )
                                })
                        } else {
                            startService(
                                context = requireContext(),
                                act = DownloadService.ACTION_START,
                                url = list[position].linkM3u8,
                                slug = this.slug,
                                movieName = detailMovie?.movie?.name,
                                position = position,
                                downloadMode = getDownloadMode()
                            )
                        }
                    }

                    else -> {

                    }
                }

            },
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        initObserver()
        binding.down.setOnClickListener {
            val activity = activity as DetailMovieActivity
            activity.showInfo()
        }

        binding.addPlayList.setOnClickListener {
            showBottomDialog()
        }

    }

    private fun showBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView =
            layoutInflater.inflate(R.layout.layout_bottom_sheet_add_play_list, null)
        val recyclerView: RecyclerView = bottomSheetView.findViewById(R.id.recyclerView)
        val accept: TextView = bottomSheetView.findViewById(R.id.accept)
        val allPlayList = viewModel.allPlayLists
        val adapter = PlayListAdapter()

        recyclerView.adapter = adapter
        lifecycleScope.launchWhenStarted {
            viewModel.allPlayLists.collect { list ->
                val list = arrayListOf<PlayList>()
                for (i in allPlayList.value) {
                    list.add(PlayList(i.id, false, i.playListName))
                }
                adapter.submitList(list)
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        val addNewPlayList: LinearLayout = bottomSheetView.findViewById(R.id.addNewPlayList)
        addNewPlayList.setOnClickListener {
            showAddPlayListBottomDialog()
        }

        accept.setOnClickListener {
            viewModel.addToPlayList(
                playlistIds = getPlayListId(adapter.currentList),
                slug = slug ?: "",
                detailMovie = detailMovie!!
            )
        }

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val maxHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()
                it.layoutParams.height = maxHeight
                it.requestLayout()

                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = true
                behavior.isFitToContents = true
                behavior.maxHeight = maxHeight
            }
        }

        bottomSheetDialog.show()
    }

    private fun getPlayListId(currentList: List<PlayList>): ArrayList<Int> {
        val list = ArrayList<Int>()
        currentList.forEach {
            if (it.isCheck) {
                list.add(it.playListId)
            }
        }
        return list
    }

    private fun showAddPlayListBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView =
            layoutInflater.inflate(R.layout.layout_bottom_sheet_create_play_list, null)

        val create: TextView = bottomSheetView.findViewById(R.id.create)
        val cancel: TextView = bottomSheetView.findViewById(R.id.cancel)
        val edtPlayListName: EditText = bottomSheetView.findViewById(R.id.edtPlayListName)

        create.setOnClickListener {
            val name = edtPlayListName.text.toString()
            viewModel.createPlayList(
                name = name
            ) { id ->
                if (id > 0) {

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Tạo playlist thất bại hoặc trùng tên!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        cancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }


    private fun getDownloadMode(): String {
        val activity = activity as DetailMovieActivity
        return activity.typePlay.name
    }

    private fun getUrlDownload(type: String, position: Int): String {
        val activity = activity as DetailMovieActivity
        val videoUrl = activity.videoURL
        return when (type) {
            "VietSub" -> videoUrl?.videoVietSub?.getOrNull(position) ?: ""
            "Thuyết minh" -> videoUrl?.videoLongTieng?.getOrNull(position) ?: ""
            else -> ""
        }
    }

    fun getPlayerMode(): Int {
        return ((activity as DetailMovieActivity).findViewById<SampleControlVideo>(R.id.playerView) as SampleControlVideo).getTypeList().size
    }

    fun showChooseTypeDownloadDialog(onAccept: (type: String) -> Unit) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.layout_choose_type_download)
        val accept = dialog.findViewById<TextView>(R.id.accept)
        val cancel = dialog.findViewById<TextView>(R.id.cancel)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)


        accept.setOnClickListener {
            val checkedId = radioGroup.checkedRadioButtonId
            if (checkedId == -1) {
                Toast.makeText(context, "Vui lòng chọn nội dung tải xuống", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
                dialog.dismiss()
                onAccept(radioButton.text.toString())
            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun saveVideoDownload() {
        lifecycleScope.launch {
            viewModel.saveVideo(
                download = VideoDownload(
                    thumb = thumb ?: "",
                    name = detailMovie?.movie?.name ?: "",
                    slug = slug ?: "",
                    detailMovie = detailMovie ?: DetailMovie()
                )
            )
        }
    }


    override fun onResume() {
        binding.root.requestLayout()
        val filter = IntentFilter().apply {
            addAction(DownloadService.ACTION_UPDATE_PROGRESS)
            addAction(DownloadService.ACTION_UPDATE_STATE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity?.registerReceiver(downloadReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            ContextCompat.registerReceiver(
                requireActivity(), downloadReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
        requireContext().bindService(
            Intent(requireContext(), DownloadService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
        super.onResume()
    }

    override fun onDestroy() {
        activity?.unregisterReceiver(downloadReceiver)
        requireContext().unbindService(connection)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

    }


    private fun deleteFile(position: Int, onSuccess: () -> Unit) {
        val folder = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "$slug"
        )
        val files = folder.listFiles()
        val prefix = "Tập${position + 1}"
        var deletedAny = false

        if (files != null) {
            for (file in files) {
                if (file.name.contains(prefix)) {
                    val deleted = file.delete()
                    if (deleted) {
                        deletedAny = true
                    }
                }
            }

            if (deletedAny) {
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(context, "Không tìm thấy file để xóa", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Thư mục trống", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDialog(
        title: String,
        message: String,
        onAccept: () -> Unit,
    ) {
        val dialog = Dialog(requireContext())
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

    fun downloadVideos(url: String, position: Int) {
        val intent = Intent(context, DownloadService::class.java)
        intent.putExtra("url", url)
        intent.putExtra("slug", this.slug)
        intent.putExtra("movieName", detailMovie?.movie?.name)
        intent.putExtra("position", position)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    private fun updateCurrentVideo(position: Int, slug: String?) {
        sharedViewModel.changeVideoIndex(position)
    }

    fun updateEpisode(slug: String?, position: Int) {
        sharedViewModel.updateEpisode(slug, position)
    }

    fun updateWatchedAt() {
        val activity = activity as DetailMovieActivity
        activity.updateChangVideo(false)
    }

    private fun setData(detailMovie: DetailMovie) {
        this.detailMovie = detailMovie
        binding.tvMovieName.text = detailMovie.movie?.name
        val info = "${detailMovie.movie?.year} | ${detailMovie.movie?.episodeTotal} tập | ${
            detailMovie.movie?.country?.get(0)?.name
        } "
        binding.tvInfo.text = info
        val category = getCategory(detailMovie.movie?.category)
        binding.tvCategory.text = category
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

    private fun initObserver() {
        lifecycleScope.launch {
            sharedViewModel.videoIndex.collect {
                if (it != -1) {
                    adapter.updateCurrentVideo(it)
                }
            }
        }

    }

    fun updateData(
        list: ArrayList<ServerData>, thumb: String?, detailMovie: DetailMovie?, slug: String?
    ) {
        this.list = list
        getNewList()
        this.thumb = thumb
        adapter.submitList(list, thumb, slug)
        this.slug = slug
        if (detailMovie != null) {
            setData(detailMovie)
        }
    }

    private fun getNewList() {
        val currentQueue = downloadService?.videoDownloader?.currentQueue()
        currentQueue?.forEach { task ->
            if (list[task.position].linkM3u8 == task.url) {
                list[task.position].downloadState = DownloadService.DownloadState.QUEUED
            }
        }
    }

    private fun getListString(data: ArrayList<ServerData>): List<String> {
        val list = ArrayList<String>()
        for (i in data.indices) {
            list.add("Tập ${i + 1}")
        }

        return list
    }
}