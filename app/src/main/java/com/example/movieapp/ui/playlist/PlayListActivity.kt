package com.example.movieapp.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.movieapp.databinding.ActivityPlayListBinding
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.widgets.StartMarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PlayListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayListBinding

    private val viewModel: PlayListViewModel by viewModels()
    private lateinit var playListAdapter: PlayListAdapter
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        playListAdapter = PlayListAdapter { playList ->
            viewModel.loadVideosForPlaylist(
                playList.id
            )
        }

        videoAdapter = VideoAdapter(
            onItemClick = { video ->
                val intent = Intent(this, DetailMovieActivity::class.java)
                intent.putExtra("name", video.slug)
                intent.putExtra("type", 0)
                startActivity(intent)
            },
            onDelete = { video ->
                val selectedPostion = playListAdapter.selectedPosition
                viewModel.deleteVideo(
                    video.slug, playListAdapter.currentList[selectedPostion].id
                ) { success ->

                }
            }
        )

        binding.rvFilm.adapter = videoAdapter

        binding.rvPlayList.adapter = playListAdapter
        val marginInPx = (8 * resources.displayMetrics.density).toInt()
        binding.rvPlayList.addItemDecoration(StartMarginItemDecoration(marginInPx))
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                launch {
                    viewModel.playLists.collectLatest { list ->
                        playListAdapter.submitList(list)
                        if (list.isNotEmpty()) {
                            viewModel.loadVideosForPlaylist(list[0].id)
                        }
                    }
                }

                launch {
                    viewModel.videos.collect {
                        videoAdapter.submitList(it)
                    }
                }
            }


        }
    }
}