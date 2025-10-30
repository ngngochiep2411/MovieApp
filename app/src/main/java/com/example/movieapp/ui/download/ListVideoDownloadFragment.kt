package com.example.movieapp.ui.download

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movieapp.databinding.FragmentListVideoDownloadBinding
import com.example.movieapp.model.Category
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.Film
import com.example.movieapp.model.ServerData
import java.io.File
import kotlin.collections.forEach

class ListVideoDownloadFragment : Fragment() {

    var list: ArrayList<ServerData> = ArrayList()
    private var thumb: String? = ""
    private var detailMovie: DetailMovie? = null
    private lateinit var binding: FragmentListVideoDownloadBinding
    private lateinit var adapter: ListVideoDownloadAdapter
    private var slug: String? = null

    companion object {
        fun newInstance(): ListVideoDownloadFragment {
            val fragment = ListVideoDownloadFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListVideoDownloadBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListVideoDownloadAdapter()
        binding.recyclerView.adapter = adapter

        binding.down.setOnClickListener {
            val activity = activity as PlayVideoDownloadActivity
            activity.showInfo()
        }

    }


    fun updateData(
        list: ArrayList<ServerData>, thumb: String?, detailMovie: DetailMovie?, slug: String?
    ) {
        this.list = list
        this.thumb = thumb
        getFilm(slug)
        this.slug = slug
        if (detailMovie != null) {
            setData(detailMovie)
        }
    }

    private fun getFilm(slug: String?) {
        val list = ArrayList<Film>()
        val file = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            slug.toString()
        )

        val mp4Files = file.listFiles { f ->
            f.isFile && f.extension.equals("mp4", ignoreCase = true)
        } ?: emptyArray()


        mp4Files.forEachIndexed { index, f ->
            val film = Film(
                name = "",
                slug = slug.toString(),
                episode = f.name,
                thumb = ""
            )
            list.add(film)
        }

        adapter.submitList(list, thumb, slug)

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
}