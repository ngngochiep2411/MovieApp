package com.example.movieapp.ui.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityAllHistoryBinding
import com.example.movieapp.model.MovieHistory
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val viewModel by viewModels<HistoryViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getHistory()
        binding.back.setOnClickListener {
            finish()
        }
        binding.edit.setOnClickListener {
            if (binding.edit.text == "Chỉnh sửa") {
                binding.edit.text = "Hủy"
                historyAdapter.setCanEdit(true)
                historyAdapter.notifyDataSetChanged()
            } else if (binding.edit.text == "Hủy") {
                binding.edit.text = "Chỉnh sửa"
                historyAdapter.setCanEdit(false)
                historyAdapter.notifyDataSetChanged()
            }
        }


        historyAdapter = HistoryAdapter()
        historyAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, type: Int?, name: String?) {
                openActivity(historyAdapter.currentList[position].slug, -1)
            }
        })
        binding.recyclerView.adapter = historyAdapter

        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launchWhenResumed {
            viewModel.history.collect {
                historyAdapter.submitList(it)
            }
        }
    }

    private fun openActivity(movieName: String, type: Int) {
        val intent = Intent(this, DetailMovieActivity::class.java)
        intent.putExtra("name", movieName)
        intent.putExtra("type", type)
        startActivity(intent)
    }
}