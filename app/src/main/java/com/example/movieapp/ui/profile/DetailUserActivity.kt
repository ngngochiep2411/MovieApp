package com.example.movieapp.ui.profile

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityDetailUserBinding
import com.example.movieapp.model.UserDetail
import com.example.movieapp.util.DataStoreManager
import com.example.movieapp.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val viewModel: DetailUserViewModel by viewModels()
    private lateinit var uri: Uri

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try {
            if (it != null) {
                uri = it
            }
            Glide.with(this).load(it)
                .error(R.drawable.avatar_anonymous)
                .into(binding.avatar)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (permissions[android.Manifest.permission.READ_MEDIA_IMAGES] == true ||
                    permissions[android.Manifest.permission.READ_MEDIA_VIDEO] == true
                ) {
                    galleryLauncher.launch("image/*")
                }
            } else {
                if (permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                    permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
                ) {
                    galleryLauncher.launch("image/*")
                } else {
                    Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                }
            }
        }


    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private var userDetai: UserDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObserver()
        setOnClick()

    }

    private fun setOnClick() {
        binding.avatar.setOnClickListener {
            if (arePermissionsGranted()) {
                galleryLauncher.launch("image/*")
            } else {
                requestPermissions()
            }
        }

        binding.toolBar.findViewById<TextView>(R.id.save).setOnClickListener {
            viewModel.update(
                user_id = userDetai?.user?.id.toString(),
                name = binding.edUserName.text.toString(),
                file = Utils.uriToFile(uri, this),
                password = ""
            )
        }
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
                    Glide.with(binding.root.context).load(userDetail?.user?.avatar_url)
                        .error(R.drawable.avatar_anonymous).error(R.drawable.avatar_anonymous)
                        .into(binding.avatar)
                    binding.edUserName.setText(userDetail?.user?.name)
                    binding.tvEmail.text = userDetail?.user?.email
                    userDetai = userDetail
                }
            }
        }

    }

    private fun arePermissionsGranted(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.READ_MEDIA_VIDEO
                        ) == PackageManager.PERMISSION_GRANTED
            }

            else -> {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_VIDEO
                )
            }

            else -> {
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        requestPermissionLauncher.launch(permissionsToRequest)
    }
}