package com.example.talentara.data.model.response.talent

import com.google.gson.annotations.SerializedName

data class UpdateTalentResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("updatedTalent")
	val updatedTalent: UpdatedTalent? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class UpdatedTalent(

	@field:SerializedName("is_project_manager")
	val isProjectManager: Int? = null,

	@field:SerializedName("project_done")
	val projectDone: Int? = null,

	@field:SerializedName("languages")
	val languages: List<String?>? = null,

	@field:SerializedName("product_types")
	val productTypes: List<String?>? = null,

	@field:SerializedName("is_on_project")
	val isOnProject: Int? = null,

	@field:SerializedName("roles")
	val roles: List<String?>? = null,

	@field:SerializedName("availability")
	val availability: Int? = null,

	@field:SerializedName("tools")
	val tools: List<String?>? = null,

	@field:SerializedName("platforms")
	val platforms: List<String?>? = null
)
