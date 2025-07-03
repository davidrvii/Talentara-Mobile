package com.example.talentara.data.model.response.project

import com.google.gson.annotations.SerializedName

data class CurrentProjectResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("currentProject")
	val currentProject: List<CurrentProjectItem?>? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class CurrentProjectItem(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("product_types")
	val productTypes: String? = null,

	@field:SerializedName("project_id")
	val projectId: Int? = null,

	@field:SerializedName("status_name")
	val statusName: String? = null,

	@field:SerializedName("project_name")
	val projectName: String? = null,

	@field:SerializedName("client_name")
	val clientName: String? = null,

	@field:SerializedName("platforms")
	val platforms: String? = null
)
