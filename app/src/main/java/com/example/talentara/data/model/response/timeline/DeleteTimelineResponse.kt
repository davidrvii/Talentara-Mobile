package com.example.talentara.data.model.response.timeline

import com.google.gson.annotations.SerializedName

data class DeleteTimelineResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null,

	@field:SerializedName("deletedTimelineId")
	val deletedTimelineId: String? = null
)
