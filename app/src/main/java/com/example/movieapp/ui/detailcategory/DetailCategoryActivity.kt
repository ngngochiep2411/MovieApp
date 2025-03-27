package com.example.movieapp.ui.detailcategory

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.databinding.ActivityDetailCategoryBinding

class DetailCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCategoryBinding
    var type: String? = ""
    private val viewModel: DetailCategoryViewModel by viewModels()

    override

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.extras?.getString("type", "")
        viewModel.getData(type)
    }
}