package com.example.talentara.data.model.response.timeline

import com.google.gson.annotations.SerializedName

data class UpdateTimelineResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("updatedTimeline")
	val updatedTimeline: UpdatedTimeline? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class UpdatedTimeline(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("leader_approved")
	val leaderApproved: Any? = null,

	@field:SerializedName("project_phase")
	val projectPhase: String? = null,

	@field:SerializedName("evidance")
	val evidance: Any? = null,

	@field:SerializedName("client_approved")
	val clientApproved: Any? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("completed_date")
	val completedDate: Any? = null
)
