package com.example.talentara.data.model.response.project

import com.google.gson.annotations.SerializedName

data class ProjectAccessResponse(

	@field:SerializedName("access")
	val access: String? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
