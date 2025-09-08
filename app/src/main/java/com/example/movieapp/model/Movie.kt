package com.example.movieapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Movie(

    @field:SerializedName("episode_total")
    val episodeTotal: String? = null,

    @field:SerializedName("thumb_url")
    val thumbUrl: String = "",

    @field:SerializedName("country")
    val country: List<Country> = ArrayList(),

    @field:SerializedName("chieurap")
    val chieurap: Boolean? = null,

    @field:SerializedName("year")
    val year: Int? = null,

    @field:SerializedName("poster_url")
    val posterUrl: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("episode_current")
    val episodeCurrent: String? = null,

    @field:SerializedName("quality")
    val quality: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("sub_docquyen")
    val subDocquyen: Boolean? = null,


    @field:SerializedName("_id")
    val _id: String,

    @field:SerializedName("time")
    val time: String? = null,

    @field:SerializedName("lang")
    val lang: String? = null,

    @field:SerializedName("category")
    val category: List<Category> = ArrayList(),

    @field:SerializedName("slug")
    val slug: String? = null,

    @field:SerializedName("origin_name")
    val originName: String? = null,

    @field:SerializedName("content")
    val content: String? = null,

    @field:SerializedName("actor")
    val actor: ArrayList<String> = ArrayList(),
) : MultiViewItem, Parcelable {
    override val id: String
        get() = _id
}
