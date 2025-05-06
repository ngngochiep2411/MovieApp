package com.example.movieapp.ui.profile

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
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
import com.example.movieapp.model.User
import com.example.movieapp.model.UserDetail
import com.example.movieapp.util.DataStoreManager
import com.example.movieapp.util.LoadingDialog
import com.example.movieapp.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val viewModel: DetailUserViewModel by viewModels()
    private var uri: Uri? = null


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

    private var userDetai: User? = null

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

        binding.toolBar.findViewById<ImageView>(R.id.back).setOnClickListener {
            finish()
        }

        binding.toolBar.findViewById<TextView>(R.id.save).setOnClickListener {

            var path = ""
            var body: MultipartBody.Part? = null
            if (uri != null) {
//                path = Utils.getRealPathFromURI(uri, this)
                val file = Utils.getFileFromUri(context = this, uri = uri!!)
                val requestFile: RequestBody =
                    file!!.asRequestBody("image/*".toMediaTypeOrNull())
                body =
                    MultipartBody.Part.createFormData("avatar_url", file.name, requestFile)
            }

            viewModel.update(
                user_id = userDetai?.id.toString(),
                name = binding.edUserName.text.toString(),
                file = body,
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
                if (text.toString() != userDetai?.name) {
//                    binding.toolBar.enabledSave()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun initObserver() {
        lifecycleScope.launch {

            launch {
                viewModel.isLoading.observe(this@DetailUserActivity) {
//                    if (it) Utils.showDialog(activity = this@DetailUserActivity) else Utils.dismissDialog()
                }

            }

            launch {
                viewModel.toastMessage.observe(this@DetailUserActivity) {
                    Toast.makeText(this@DetailUserActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
            launch {
                dataStoreManager.userDetail.collect { userDetail ->
                    Utils.loadImage(binding.root.context, userDetail?.avatar_url, binding.avatar)
                    binding.edUserName.setText(userDetail?.name)
                    binding.tvEmail.text = userDetail?.email
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