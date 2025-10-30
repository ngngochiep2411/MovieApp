package com.example.movieapp.model

import android.os.Parcelable
import com.example.movieapp.service.DownloadService
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailMovie(

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("movie")
    val movie: Movie? = null,

    @field:SerializedName("episodes")
    val episodes: List<Episodes>? = null,

    @field:SerializedName("status")
    val status: Boolean? = null,
) : Parcelable

@Parcelize
data class ServerData(
    @field:SerializedName("filename")
    val filename: String? = null,

    @field:SerializedName("link_embed")
    val linkEmbed: String? = null,

    @field:SerializedName("link_m3u8")
    val linkM3u8: String = "",

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("slug")
    val slug: String? = null,
    var progress: Int = 0,
    var downloadState: DownloadService.DownloadState = DownloadService.DownloadState.IDLE,
) : Parcelable

@Parcelize
data class Episodes(

    @field:SerializedName("server_name")
    val serverName: String? = null,

    @field:SerializedName("server_data")
    val serverData: ArrayList<ServerData>
) : Parcelable

