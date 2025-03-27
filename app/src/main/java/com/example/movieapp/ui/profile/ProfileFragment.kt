package com.example.movieapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.adapters.ViewBindingAdapter.setOnClick
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentProfileBinding
import com.example.movieapp.ui.auth.BottomSheetAuthFragment
import com.example.movieapp.util.DataStoreManager
import com.example.movieapp.util.Utils
import com.example.movieapp.util.Utils.Companion.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var dataStoreManager: DataStoreManager

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

    }

    private fun setOnClick() {
        binding.info.setOnClickListener {
            val intent = Intent(context, DetailUserActivity::class.java)
            startActivity(intent)
        }

        binding.tvLogOut.setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.logout()
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
                dataStoreManager.userDetail.collect { userDetail ->
                    Glide.with(binding.root.context).load(userDetail?.user?.avatarUrl)
                        .error(R.drawable.avatar_anonymous)
                        .placeholder(R.drawable.avatar_anonymous)
                        .into(binding.avatar)
                    binding.tvUserName.text = userDetail?.user?.name

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