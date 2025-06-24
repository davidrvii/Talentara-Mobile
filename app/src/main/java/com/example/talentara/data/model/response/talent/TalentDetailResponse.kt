package com.example.talentara.data.model.response.talent

import com.google.gson.annotations.SerializedName

data class TalentDetailResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("talentDetail")
	val talentDetail: TalentDetailItem? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class TalentDetailItem(

	@field:SerializedName("user_email")
	val userEmail: String? = null,

	@field:SerializedName("github")
	val github: String? = null,

	@field:SerializedName("languages")
	val languages: String? = null,

	@field:SerializedName("product_types")
	val productTypes: String? = null,

	@field:SerializedName("user_image")
	val userImage: String? = null,

	@field:SerializedName("is_on_project")
	val isOnProject: Int? = null,

	@field:SerializedName("user_name")
	val userName: String? = null,

	@field:SerializedName("roles")
	val roles: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("talent_avg_rating")
	val talentAvgRating: String? = null,

	@field:SerializedName("availability")
	val availability: Int? = null,

	@field:SerializedName("linkedin")
	val linkedin: String? = null,

	@field:SerializedName("tools")
	val tools: String? = null,

	@field:SerializedName("platforms")
	val platforms: String? = null,

	@field:SerializedName("is_project_manager")
	val isProjectManager: Int? = null,

	@field:SerializedName("project_done")
	val projectDone: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("project_declined")
	val projectDeclined: Int? = null,

	@field:SerializedName("talent_id")
	val talentId: Int? = null
)
