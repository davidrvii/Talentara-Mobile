package com.example.talentara.data.model.response.project

import com.google.gson.annotations.SerializedName

data class ProjectDetailResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("projectDetail")
	val projectDetail: List<ProjectDetailItem?>? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class ProjectDetailItem(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("languages")
	val languages: String? = null,

	@field:SerializedName("product_types")
	val productTypes: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("project_github")
	val projectGithub: String? = null,

	@field:SerializedName("project_name")
	val projectName: String? = null,

	@field:SerializedName("tools")
	val tools: String? = null,

	@field:SerializedName("project_desc")
	val projectDesc: String? = null,

	@field:SerializedName("completed_date")
	val completedDate: String? = null,

	@field:SerializedName("platforms")
	val platforms: String? = null,

	@field:SerializedName("features")
	val features: String? = null,

	@field:SerializedName("user_target")
	val userTarget: String? = null,

	@field:SerializedName("status_id")
	val statusId: Int? = null,

	@field:SerializedName("status_name")
	val statusName: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("project_id")
	val projectId: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("meet_link")
	val meetLink: String? = null,

	@field:SerializedName("client_name")
	val clientName: String? = null,

	@field:SerializedName("talents")
	val talents: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
)
