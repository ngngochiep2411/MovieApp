package com.example.movieapp.model

data class SearchResultMovie(
    val category: List<CategoryMovie>,
    val country: List<CountryMovie>,
    val movies: ArrayList<Movie?>
)
