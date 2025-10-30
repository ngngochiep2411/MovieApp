package com.example.movieapp.ui.comment.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentImageViewerBinding


class ImageViewerFragment : Fragment() {


    private lateinit var binding: FragmentImageViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false)
        return binding.root
    }


}