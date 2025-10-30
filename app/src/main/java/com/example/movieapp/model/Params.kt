package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class Params(

    @field:SerializedName("pagination")
    val pagination: Pagination? = null,

    @field:SerializedName("type_slug")
    val typeSlug: String? = null,

    @field:SerializedName("filterYear")
    val filterYear: Any,

    @field:SerializedName("sortType")
    val sortType: String? = null,

    @field:SerializedName("filterCountry")
    val filterCountry: List<String?>? = null,

    @field:SerializedName("filterCategory")
    val filterCategory: List<String?>? = null,

    @field:SerializedName("sortField")
    val sortField: String? = null,

    @field:SerializedName("filterType")
    val filterType: Any
)