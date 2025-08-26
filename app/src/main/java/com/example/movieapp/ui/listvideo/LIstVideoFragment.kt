package com.example.movieapp.ui.listvideo

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentLIstVideoBinding
import com.example.movieapp.model.Category
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.ServerData
import com.example.movieapp.service.DownloadService
import com.example.movieapp.ui.authen.LoginActivity
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.ui.listvideo.adapter.FlexboxAdapter
import com.example.movieapp.ui.listvideo.adapter.ListVideoAdapter
import com.example.movieapp.ui.listvideo.adapter.ListVideoAdapter2
import com.example.movieapp.util.Extension.parcelable
import com.example.movieapp.util.Extension.parcelableArrayList
import com.example.movieapp.util.SharedViewModel
import com.example.movieapp.util.VideoDownloader
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
    private var detailMovie: DetailMovie? = null
    private lateinit var flexboxAdapter: FlexboxAdapter
    private var slug: String = ""
    var privateDir: File? = null
    private lateinit var videoDownloader: VideoDownloader

    companion object {
        fun newInstance(
            list: ArrayList<ServerData>,
            thumb: String?,
            detailMovie: DetailMovie?
        ): LIstVideoFragment {
            val fragment = LIstVideoFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("data", list)
            bundle.putString("thumb", thumb)
            bundle.putParcelable("detailMovie", detailMovie)
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
        list = arguments?.parcelableArrayList("data")!!
        thumb = arguments?.getString("thumb").toString()
        detailMovie = arguments?.parcelable("detailMovie")
        if (detailMovie != null) {
            setData(detailMovie!!)
        }
        arguments?.getString("thumb")
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
                downloadVideos(arrayListOf(this.list[position]))
            },
            onDeleteClick = { position ->
                showDialog(
                    title = "Xóa nội dung đã tải?",
                    message = "Nội dung này sẽ không có sẵn trên thiết bị để xem khi không có Internet.",
                    onAccept = {
                        deleteFile(position)
                    }
                )
            },
            onRemoveQUEUED = {
                showDialog(
                    title = "Hủy tải xuống?",
                    message = "Nội dung này đang được chờ để được tải xuống. Bạn có muốn hủy bỏ việc tải xuống nội dung này?",
                    onAccept = {

                    }
                )
            }
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        initObserver()
        binding.download.setOnClickListener {
            downloadVideos(this.list)
        }
    }

    private fun deleteFile(position: Int) {
        val privateDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(privateDir, "$slug/Tập${position + 1}.mp4")
        if (file.exists()) {
            val deleted = file.delete()
            if (deleted) {
                Toast.makeText(
                    context,
                    "Xóa thành công",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Xóa thất bại",
                    Toast.LENGTH_SHORT
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

    fun downloadVideos(list: ArrayList<ServerData>) {
        val intent = Intent(context, DownloadService::class.java)
        intent.putParcelableArrayListExtra("urls", list)
        intent.putExtra("slug", this.slug)
        intent.putExtra("movieName", detailMovie?.movie?.name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    private fun updateCurrentVideo(position: Int, slug: String) {
        sharedViewModel.changeVideoIndex(position)
    }

    fun updateEpisode(slug: String, position: Int) {
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
        binding.content.text =
            detailMovie.movie?.content.toString()
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

    fun updateList(
        list: ArrayList<ServerData>,
        thumb: String?,
        detailMovie: DetailMovie?,
        slug: String
    ) {
        this.list = list
        adapter.submitList(list, thumb,slug)
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

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

}