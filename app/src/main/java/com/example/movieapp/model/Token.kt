package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class Token(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)