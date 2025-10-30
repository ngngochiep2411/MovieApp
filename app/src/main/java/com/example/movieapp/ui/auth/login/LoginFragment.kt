package com.example.movieapp.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentLoginBinding
import com.example.movieapp.ui.auth.BottomSheetAuthFragment
import com.example.movieapp.ui.category.MovieSearchResultViewModel
import com.example.movieapp.util.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setOnClick()

        initObserver()
    }

    private fun initObserver() {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.login.observe(viewLifecycleOwner) {
            if (it) {
                dissmissBottomSheet()
            }
        }
    }

    private fun dissmissBottomSheet() {
        val bottomSheet =
            activity?.supportFragmentManager?.findFragmentByTag(BottomSheetAuthFragment.TAG)
        if (bottomSheet != null) {
            (bottomSheet as BottomSheetAuthFragment).dismiss()
        }
    }

    private fun setOnClick() {
        binding.btnLogin.setOnClickListener {
            viewModel.login(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    companion object {
        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}