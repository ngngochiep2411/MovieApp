package com.example.movieapp.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityDetailUserBinding
import com.example.movieapp.model.UserDetail
import com.example.movieapp.util.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private var userDetai: UserDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObserver()


    }

    private fun initView() {
        binding.edUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (text.toString() != userDetai?.user?.name) {
                    binding.toolBar.enabledSave()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun initObserver() {
        lifecycleScope.launch {

           launch {
                dataStoreManager.userDetail.collect { userDetail ->
                    Glide.with(binding.root.context).load(userDetail?.user?.avatarUrl)
                        .error(R.drawable.avatar_anonymous).error(R.drawable.avatar_anonymous)
                        .into(binding.avatar)
                    binding.edUserName.setText(userDetail?.user?.name)
                    binding.tvEmail.text = userDetail?.user?.email
                    userDetai = userDetail
                }
            }
        }

    }
}