package com.example.movieapp.util

import androidx.room.TypeConverter
import com.example.movieapp.model.DetailMovie
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromVideoInfo(detailMovie: DetailMovie?): String? {
        return detailMovie?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toVideoInfo(json: String?): DetailMovie? {
        return json?.let { Gson().fromJson(it, DetailMovie::class.java) }
    }
}