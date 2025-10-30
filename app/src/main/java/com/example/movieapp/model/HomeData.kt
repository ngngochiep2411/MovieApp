package com.example.movieapp.model

data class HomeData(
    val movies: Movies?,
    val seriesMovie: SeriesMovie?,
    val cartoonMovie: CartoonMovie?,
    val tvShowsMovie: TvShowsMovie?,
    val newMovie: NewMovie?,
    val viet_sub: Movies?,
    val thuyet_minh: Movies?,
    val long_tieng: Movies?,
)
