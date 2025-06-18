package com.example.talentara.data.model.response.notification

import com.google.gson.annotations.SerializedName

data class NewNotificationResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("newNotification")
	val newNotification: NewNotification? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class NewNotification(

	@field:SerializedName("notification_desc")
	val notificationDesc: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("notification_title")
	val notificationTitle: String? = null
)
