package com.example.movieapp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.FragmentProfileBinding
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.ui.auth.BottomSheetAuthFragment
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.ui.download.VideoDownloadActivity
import com.example.movieapp.ui.favorite.FavoriteActivity
import com.example.movieapp.ui.history.AllHistoryActivity
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.ui.playlist.PlayListActivity
import com.example.movieapp.ui.profile.adapter.MovieViewHistoryAdapter
import com.example.movieapp.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var historyAdapter: MovieViewHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    Glide.with(this)
                        .load(uri)
                        .into(binding.avatar)
                }
            }
        historyAdapter = MovieViewHistoryAdapter()
        historyAdapter.setOnItemClickListener(this)
        binding.rvHistory.apply {
            adapter = historyAdapter
            setHasFixedSize(true)
        }
        setOnClick()
        initObserver()
    }


    private fun setOnClick() {

        binding.info.setOnClickListener {
            val intent = Intent(context, DetailUserActivity::class.java)
            startActivity(intent)
        }

        binding.tvLogOut.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logOut()
            }
        }

        binding.tvSignInout.setOnClickListener {
            val bottomSheetFragment = BottomSheetAuthFragment()
            bottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                BottomSheetAuthFragment.TAG
            )
        }

        binding.recent.setOnClickListener {
            val intent = Intent(context, AllHistoryActivity::class.java)
            startActivity(intent)
        }
        binding.download.setOnClickListener {
            val intent = Intent(context, VideoDownloadActivity::class.java)
            startActivity(intent)

        }

        binding.favorite.setOnClickListener {
            val intent = Intent(context, FavoriteActivity::class.java)
            startActivity(intent)

        }

        binding.playList.setOnClickListener {
            val intent = Intent(context, PlayListActivity::class.java)
            startActivity(intent)

        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            launch {
                viewModel.userDetail.collect { userDetail ->
                    Utils.loadImage(requireContext(), userDetail?.avatar_url, binding.avatar)
                    binding.tvUserName.text = userDetail?.name
                    binding.info.visibility = if (userDetail != null) View.VISIBLE else View.GONE
                    binding.tvSignInout.visibility =
                        if (userDetail != null) View.GONE else View.VISIBLE
                }
            }

            launch {
                viewModel.history.collect {
                    historyAdapter.submitList(it)
                }
            }
        }

    }

    private fun openActivity(movieName: String, type: Int) {
        val intent = Intent(context, DetailMovieActivity::class.java)
        intent.putExtra("name", movieName)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onItemClick(position: Int, type: Int?, name: String?) {
        val name = (historyAdapter.currentList[position] as MovieHistory).slug
        openActivity(movieName = name.toString(), type = 0)
    }
}