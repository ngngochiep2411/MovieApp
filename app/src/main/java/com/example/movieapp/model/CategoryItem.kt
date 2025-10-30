package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class CategoryItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("slug")
	val slug: String? = null
)