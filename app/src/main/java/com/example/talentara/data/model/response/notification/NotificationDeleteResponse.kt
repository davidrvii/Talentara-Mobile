package com.example.talentara.data.model.response.notification

import com.google.gson.annotations.SerializedName

data class NotificationDeleteResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("deletedNotificationId")
	val deletedNotificationId: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
