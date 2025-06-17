package com.example.movieapp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.FragmentProfileBinding
import com.example.movieapp.ui.auth.BottomSheetAuthFragment
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.util.Utils
import com.example.movieapp.util.Utils.Companion.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var databaseManager: DatabaseManager

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutParams = binding.main.layoutParams as FrameLayout.LayoutParams
        layoutParams.topMargin = Utils.getStatusBarHeight(requireContext()) + 20

        initObserver()

        setOnClick()

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    Glide.with(this)
                        .load(uri)
                        .into(binding.avatar)
                }
            }

    }


    private fun setOnClick() {

        binding.info.setOnClickListener {
            val intent = Intent(context, DetailUserActivity::class.java)
            startActivity(intent)
        }

        binding.tvLogOut.setOnClickListener {
            lifecycleScope.launch {
                databaseManager.logout()
            }
        }

        binding.tvSignInout.setOnClickListener {
            val bottomSheetFragment = BottomSheetAuthFragment()
            bottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                BottomSheetAuthFragment.TAG
            )
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            launch {
                viewModel.userDetail.collect { userDetail ->
                    Log.d("testing", "avatarurl ${userDetail?.avatar_url}")
                    Utils.loadImage(requireContext(), userDetail?.avatar_url, binding.avatar)
                    binding.tvUserName.text = userDetail?.name
                    binding.info.visibility = if (userDetail != null) View.VISIBLE else View.GONE
                    binding.tvSignInout.visibility =
                        if (userDetail != null) View.GONE else View.VISIBLE
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.transparentStatusBar()
    }
}