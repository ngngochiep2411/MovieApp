package com.example.movieapp.ui.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.movieapp.databinding.FragmentSignInBinding
import com.example.movieapp.ui.auth.BottomSheetAuthFragment
import com.example.movieapp.util.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            launch {
                launch {

                    viewModel.register.observe(viewLifecycleOwner) {
                        if (it) {
                            val email = binding.edtEmail.text.toString()
                            val password = binding.edtPassword.text.toString()
                            val name = binding.edtUserName.text.toString()
                            binding.edtEmail.text.clear()
                            binding.edtPassword.text.clear()
                            binding.edtUserName.text.clear()
                            switchPage(email, password, name)
                        }
                    }
                }

                launch {
                    viewModel.toastMessage.observe(viewLifecycleOwner) {
                        Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnSignIn.setOnClickListener {
            viewModel.register(
                binding.edtEmail.text.toString(),
                binding.edtPassword.text.toString(),
                binding.edtUserName.text.toString()
            )
        }
    }

    private fun switchPage(email: String, password: String, name: String) {
        val bottomSheet =
            activity?.supportFragmentManager?.findFragmentByTag(BottomSheetAuthFragment.TAG)
        if (bottomSheet != null) {
            val viewPager2 = (bottomSheet as BottomSheetAuthFragment).getViewPager()
            viewPager2.currentItem = 0
        }
    }


    companion object {
        fun newInstance(): SignInFragment {
            val fragment = SignInFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}