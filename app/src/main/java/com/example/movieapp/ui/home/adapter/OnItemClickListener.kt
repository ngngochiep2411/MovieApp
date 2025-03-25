package com.example.movieapp.ui.home.adapter

interface OnItemClickListener {
    fun onItemClick(position: Int, type: Int? = null, name: String? = null)
}