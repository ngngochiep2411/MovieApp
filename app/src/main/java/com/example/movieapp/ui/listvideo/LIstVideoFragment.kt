package com.example.movieapp.ui.listvideo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.movieapp.databinding.FragmentLIstVideoBinding
import com.example.movieapp.model.Category
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.ServerData
import com.example.movieapp.ui.listvideo.adapter.FlexboxAdapter
import com.example.movieapp.ui.listvideo.adapter.ListVideoAdapter
import com.example.movieapp.ui.listvideo.adapter.ListVideoAdapter2
import com.example.movieapp.util.Extension.parcelable
import com.example.movieapp.util.Extension.parcelableArrayList
import com.example.movieapp.util.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LIstVideoFragment : Fragment() {

    private lateinit var binding: FragmentLIstVideoBinding
    private lateinit var adapter: ListVideoAdapter2
    var list: ArrayList<ServerData> = ArrayList()
    private var thumb: String = ""
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var detailMovie: DetailMovie? = null
    private lateinit var flexboxAdapter: FlexboxAdapter

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

        list = arguments?.parcelableArrayList("data")!!
        thumb = arguments?.getString("thumb").toString()
        detailMovie = arguments?.parcelable("detailMovie")
        if (detailMovie != null) {
            setData(detailMovie!!)
        }
        arguments?.getString("thumb")
        adapter = ListVideoAdapter2(list, thumb) { position ->
            sharedViewModel.changeVideoIndex(position)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        initObserver()


    }

    private fun setData(detailMovie: DetailMovie) {
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

    fun updateList(list: ArrayList<ServerData>, thumb: String?, detailMovie: DetailMovie?) {
        adapter.submitList(list, thumb)
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