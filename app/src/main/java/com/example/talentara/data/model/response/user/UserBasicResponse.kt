package com.example.talentara.data.model.response.user

import com.google.gson.annotations.SerializedName

data class UserBasicResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("usersBasic")
	val usersBasic: List<UsersBasicItem?>? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class UsersBasicItem(

	@field:SerializedName("user_image")
	val userImage: String? = null,

	@field:SerializedName("user_name")
	val userName: String? = null
)
