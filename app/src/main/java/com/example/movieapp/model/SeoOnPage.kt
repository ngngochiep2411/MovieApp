package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class SeoOnPage(

	@field:SerializedName("descriptionHead")
	val descriptionHead: String? = null,

	@field:SerializedName("titleHead")
	val titleHead: String? = null,

	@field:SerializedName("og_type")
	val ogType: String? = null,

	@field:SerializedName("og_url")
	val ogUrl: String? = null,

	@field:SerializedName("og_image")
	val ogImage: List<String?>? = null
)