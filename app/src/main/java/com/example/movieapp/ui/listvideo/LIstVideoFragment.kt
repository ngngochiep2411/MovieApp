package com.example.movieapp.ui.listvideo

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentLIstVideoBinding
import com.example.movieapp.model.Category
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.ServerData
import com.example.movieapp.model.VideoDownload
import com.example.movieapp.service.DownloadService
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.ui.listvideo.adapter.ListVideoAdapter
import com.example.movieapp.util.Extension.parcelable
import com.example.movieapp.util.Extension.parcelableArrayList
import com.example.movieapp.util.SharedViewModel
import com.example.movieapp.util.VideoDownloader
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.play.integrity.internal.ac
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class LIstVideoFragment : Fragment() {

    private lateinit var binding: FragmentLIstVideoBinding
    private lateinit var adapter: ListVideoAdapter
    var list: ArrayList<ServerData> = ArrayList()
    private var thumb: String = ""
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: ListVideoViewModel by viewModels()
    private var detailMovie: DetailMovie? = null
    private var slug: String? = ""
    private lateinit var videoDownloader: VideoDownloader

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {
                DownloadService.ACTION_UPDATE_PROGRESS -> {

                    val index = intent.getIntExtra(DownloadService.EXTRA_INDEX, -1)
                    val progress = intent.getDoubleExtra(DownloadService.EXTRA_PROGRESS, 0.0)
                    if (index >= 0) {
                        adapter.updateProgress(index, progress)
                    }
                }

                DownloadService.ACTION_UPDATE_STATE -> {
                    val state = intent.getStringExtra(DownloadService.EXTRA_STATE)
                    val index = intent.getIntExtra(DownloadService.EXTRA_INDEX, -1)
                    adapter.updateState(state, index)
                }
            }
        }
    }

    companion object {
        fun newInstance(
            list: ArrayList<ServerData>, thumb: String?, detailMovie: DetailMovie?
        ): LIstVideoFragment {
            val fragment = LIstVideoFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("data", list)
            bundle.putString("thumb", thumb)
            bundle.putParcelable("detailMovie", detailMovie)
            fragment.arguments = bundle
            return fragment
        }

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

    override fun onStart() {
        super.onStart()
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
                if (state == DownloadService.DownloadState.DOWNLOADED) {
                    showDialog(
                        title = "Xóa nội dung đã tải?",
                        message = "Nội dung này sẽ không có sẵn trên thiết bị để xem khi không có Internet.",
                        onAccept = {
                            deleteFile(
                                position, onSuccess = {
                                    sendBroadCast(
                                        action = DownloadService.ACTION_UPDATE_STATE,
                                        position = position,
                                        state = DownloadService.DownloadState.IDLE
                                    )
                                    startService(
                                        act = DownloadService.ACTION_REMOVE_QUEUE,
                                        url = list[position].linkM3u8,
                                        slug = this.slug,
                                        movieName = detailMovie?.movie?.name,
                                        position = position
                                    )
                                })
                        })
                } else if (state == DownloadService.DownloadState.QUEUED) {
                    showDialog(
                        title = "Hủy tải xuống?",
                        message = "Nội dung này đang được chờ để được tải xuống. Bạn có muốn hủy bỏ việc tải xuống nội dung này?",
                        onAccept = {
                            sendBroadCast(
                                action = DownloadService.ACTION_UPDATE_STATE,
                                position = position,
                                state = DownloadService.DownloadState.IDLE
                            )
                            startService(
                                act = DownloadService.ACTION_REMOVE_QUEUE,
                                url = list[position].linkM3u8,
                                slug = this.slug,
                                movieName = detailMovie?.movie?.name,
                                position = position
                            )
                        })
                } else {
                    startService(
                        act = DownloadService.ACTION_START,
                        url = list[position].linkM3u8,
                        slug = this.slug,
                        movieName = detailMovie?.movie?.name,
                        position = position
                    )
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

    }

    private fun saveVideoDownload() {
        lifecycleScope.launch {
            viewModel.saveVideo(
                download = VideoDownload(
                    thumb = thumb,
                    name = detailMovie?.movie?.name ?: "",
                    slug = slug ?: ""
                )
            )
        }
    }

    fun startService(
        act: String,
        url: String = "",
        slug: String? = "",
        movieName: String? = null,
        position: Int = -1
    ) {
        val intent = Intent(requireContext(), DownloadService::class.java).apply {
            setPackage(requireContext().packageName)
            action = act
            if (url.isNotEmpty()) {
                putExtra(DownloadService.EXTRA_URL, url)
            }
            if (!slug.isNullOrEmpty()) {
                putExtra(DownloadService.EXTRA_SLUG, slug)
            }
            if (!movieName.isNullOrEmpty()) {
                putExtra(DownloadService.EXTRA_MOVIE_NAME, movieName)
            }
            if (position != -1) {
                putExtra(DownloadService.EXTRA_POSITION, position)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    fun sendBroadCast(
        action: String,
        state: DownloadService.DownloadState? = null,
        position: Int = -1,
        url: String = "",
        slug: String = "",
        movieName: String? = null
    ) {
        val intent = Intent(action).apply {
            setPackage(requireContext().packageName)
            if (state != null) {
                putExtra(DownloadService.EXTRA_STATE, state)
            }
            if (position != -1) {
                putExtra(DownloadService.EXTRA_INDEX, position)
            }
            if (url.isNotEmpty()) {
                putExtra(DownloadService.EXTRA_URL, url)
            }
            if (slug.isNotEmpty()) {
                putExtra(DownloadService.EXTRA_SLUG, slug)
            }
            if (!movieName.isNullOrEmpty()) {
                putExtra(DownloadService.EXTRA_MOVIE_NAME, slug)
            }
        }
        requireContext().sendBroadcast(intent)
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
            activity?.registerReceiver(downloadReceiver, filter)
        }
        super.onResume()
    }

    override fun onDestroy() {
        activity?.unregisterReceiver(downloadReceiver)
        super.onDestroy()
    }

    private fun deleteFile(position: Int, onSuccess: () -> Unit) {
        val privateDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(privateDir, "$slug/Tập${position + 1}.mp4")
        if (file.exists()) {
            val deleted = file.delete()
            if (deleted) {
                Toast.makeText(
                    context, "Xóa thành công", Toast.LENGTH_SHORT
                ).show()
                onSuccess()
            } else {
                Toast.makeText(
                    context, "Xóa thất bại", Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(context, "Nội dung này không còn tồn tại trên máy", Toast.LENGTH_SHORT)
                .show()
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
        Log.d("testing", "updateList")
        this.list = list
        adapter.submitList(list, thumb, slug)
        this.slug = slug
        if (detailMovie != null) {
            setData(detailMovie)
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