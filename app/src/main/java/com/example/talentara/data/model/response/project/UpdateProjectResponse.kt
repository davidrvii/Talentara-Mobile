package com.example.talentara.data.model.response.project

import com.google.gson.annotations.SerializedName

data class UpdateProjectResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null,

	@field:SerializedName("updatedProject")
	val updatedProject: UpdatedProject? = null
)

data class FixRoleItem(

	@field:SerializedName("role_name")
	val roleName: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null
)

data class UpdatedProject(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("product_type")
	val productType: List<String?>? = null,

	@field:SerializedName("role")
	val role: List<FixRoleItem?>? = null,

	@field:SerializedName("status_id")
	val statusId: Int? = null,

	@field:SerializedName("feature")
	val feature: List<String?>? = null,

	@field:SerializedName("language")
	val language: List<String?>? = null,

	@field:SerializedName("project_name")
	val projectName: String? = null,

	@field:SerializedName("client_name")
	val clientName: String? = null,

	@field:SerializedName("tools")
	val tools: List<String?>? = null,

	@field:SerializedName("platform")
	val platform: List<String?>? = null,

	@field:SerializedName("project_desc")
	val projectDesc: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
)
