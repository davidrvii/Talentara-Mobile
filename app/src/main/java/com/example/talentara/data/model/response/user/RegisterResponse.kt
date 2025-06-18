package com.example.talentara.data.model.response.user

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("registResult")
	val registResult: RegistResult? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class RegistResult(

	@field:SerializedName("user_email")
	val userEmail: String? = null,

	@field:SerializedName("user_password")
	val userPassword: String? = null,

	@field:SerializedName("user_name")
	val userName: String? = null
)
