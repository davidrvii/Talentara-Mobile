package com.example.talentara.data.model.response.user

import com.google.gson.annotations.SerializedName

data class DeleteUserResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("deletedUserId")
	val deletedUserId: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
