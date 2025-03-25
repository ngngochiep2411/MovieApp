package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class Data(

    @field:SerializedName("seoOnPage")
    val seoOnPage: SeoOnPage? = null,

    @field:SerializedName("type_list")
    val typeList: String? = null,

    @field:SerializedName("APP_DOMAIN_FRONTEND")
    val aPPDOMAINFRONTEND: String? = null,

    @field:SerializedName("titlePage")
    val titlePage: String? = null,

    @field:SerializedName("breadCrumb")
    val breadCrumb: List<BreadCrumbItem?>? = null,

    @field:SerializedName("params")
    val params: Params? = null,

    @field:SerializedName("APP_DOMAIN_CDN_IMAGE")
    val aPPDOMAINCDNIMAGE: String? = null,

    @field:SerializedName("items")
    val items: ArrayList<Movie?> = ArrayList(),
)