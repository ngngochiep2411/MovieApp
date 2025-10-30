package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class BreadCrumbItem(

	@field:SerializedName("isCurrent")
	val isCurrent: Boolean? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("position")
	val position: Int? = null,

	@field:SerializedName("slug")
	val slug: String? = null
)