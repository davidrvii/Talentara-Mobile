package com.example.talentara.data.model.response.timeline

import com.google.gson.annotations.SerializedName

data class TimelineDetailResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null,

	@field:SerializedName("timelineDetail")
	val timelineDetail: List<TimelineDetailItem?>? = null
)

data class TimelineDetailItem(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("project_id")
	val projectId: Int? = null,

	@field:SerializedName("leader_approved")
	val leaderApproved: Int? = null,

	@field:SerializedName("project_phase")
	val projectPhase: String? = null,

	@field:SerializedName("evidance")
	val evidence: String? = null,

	@field:SerializedName("timeline_id")
	val timelineId: Int? = null,

	@field:SerializedName("client_approved")
	val clientApproved: Int? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("completed_date")
	val completedDate: String? = null
)
