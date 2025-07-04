package com.example.talentara.data.model.response.timeline

import com.google.gson.annotations.SerializedName

data class TimelineApprovementResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("approvement")
	val approvement: List<ApprovementItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class ApprovementItem(

	@field:SerializedName("leader_approved")
	val leaderApproved: Int? = null,

	@field:SerializedName("client_approved")
	val clientApproved: Int? = null
)
