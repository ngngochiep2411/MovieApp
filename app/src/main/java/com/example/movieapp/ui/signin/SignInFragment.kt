package com.example.movieapp.ui.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentLoginBinding
import com.example.movieapp.databinding.FragmentSignInBinding
import com.example.movieapp.ui.login.LoginViewModel
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.register(
                    binding.edtEmail.text.toString(),
                    binding.edtPassword.text.toString(),
                    binding.edtUserName.text.toString()
                ).collect {
                    if (it.success()) {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

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