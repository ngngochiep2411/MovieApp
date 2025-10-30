package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class UserDetail(

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("token")
	val token: Token? = null
)