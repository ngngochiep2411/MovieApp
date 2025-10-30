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
    private var listCheck = ArrayList<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getHistory()
        binding.back.setOnClickListener {
            finish()
        }
        binding.checkAll.setOnClickListener {
            checkAll()
        }

        binding.delete.setOnClickListener {
            delete()
        }
        binding.edit.setOnClickListener {
            if (binding.edit.text == "Chỉnh sửa") {
                binding.edit.text = "Hủy"
                historyAdapter.setCanEdit(true)
                historyAdapter.resetListChecked()
                historyAdapter.notifyDataSetChanged()
            } else if (binding.edit.text == "Hủy") {
                binding.edit.text = "Chỉnh sửa"
                historyAdapter.resetListChecked()
                historyAdapter.setCanEdit(false)
                historyAdapter.notifyDataSetChanged()
            }
        }




        historyAdapter = HistoryAdapter(listCheck = listCheck)
        historyAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, type: Int?, name: String?) {
                openActivity(historyAdapter.currentList[position].slug, -1)
            }
        })
        binding.recyclerView.adapter = historyAdapter

        initObserver()
    }

    fun checkAll() {
        for (i in listCheck.indices) {
            listCheck[i] = true
        }
        val newList = historyAdapter.currentList.toMutableList()
        historyAdapter.setCanEdit(true)

//        historyAdapter.submitList(newList)
        historyAdapter.notifyDataSetChanged()
    }

    fun delete() {
        val listDelete = ArrayList<String>()
        for (i in listCheck.indices) {
            if (listCheck[i]) {
                listDelete.add(historyAdapter.currentList[i].slug)
            }
        }
        listCheck.clear()
        viewModel.deleteMovies(listDelete)
    }

    private fun initObserver() {
        lifecycleScope.launchWhenResumed {
            viewModel.history.collect {
                if (listCheck.isNotEmpty()) {
                    listCheck.clear()
                }
                for (i in it.indices) {
                    listCheck.add(false)
                }
                historyAdapter.submitList(it)
            }

            viewModel.movieList.collect { updatedList ->
                historyAdapter.submitList(updatedList)
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