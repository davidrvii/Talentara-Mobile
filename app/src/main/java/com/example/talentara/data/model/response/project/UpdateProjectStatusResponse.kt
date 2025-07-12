package com.example.talentara.data.model.response.project

import com.google.gson.annotations.SerializedName

data class UpdateProjectStatusResponse(

	@field:SerializedName("updatedProjectStatus")
	val updatedProjectStatus: UpdatedProjectStatus? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class UpdatedProjectStatus(

	@field:SerializedName("statusId")
	val statusId: Int? = null,

	@field:SerializedName("projectId")
	val projectId: String? = null
)
