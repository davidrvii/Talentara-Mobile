package com.example.talentara.data.model.response.timeline

import com.google.gson.annotations.SerializedName

data class NewTimelineResponse(

	@field:SerializedName("newTimeline")
	val newTimeline: NewTimeline? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class NewTimeline(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("project_id")
	val projectId: Int? = null,

	@field:SerializedName("project_phase")
	val projectPhase: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
)
