package com.example.movieapp.ui.search

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.Assertions.checkMainThread
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.ActivitySearchBinding
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.ui.home.adapter.OnItemClickListener
import com.example.movieapp.util.Utils
import com.example.movieapp.widgets.CustomItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClick()
        initView()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.movies.collect {
                    searchAdapter.submitList(it)
                }
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    private fun initView() {

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (runnable != null) {
                    handler.removeCallbacks(runnable!!)
                }

                runnable = Runnable {
                    viewModel.search(s.toString())
                }
                handler.postDelayed(runnable!!, 100)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        historyAdapter = HistoryAdapter()



        searchAdapter = SearchAdapter()
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                outRect.bottom = 10
                val column = position % 3
                if (column == 0 || column == 1) {
                    outRect.right = 10
                }
            }
        })
        binding.recyclerView.adapter = searchAdapter
        searchAdapter.setOnItemClickListener(this)
    }

    private fun setOnClick() {
        binding.back.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(position: Int, type: Int?, name: String?) {
        openActivity(movieName = name!!, type = 0)
    }

    private fun openActivity(movieName: String, type: Int) {
        val intent = Intent(this, DetailMovieActivity::class.java)
        intent.putExtra("name", movieName)
        intent.putExtra("type", type)
        startActivity(intent)
    }
}