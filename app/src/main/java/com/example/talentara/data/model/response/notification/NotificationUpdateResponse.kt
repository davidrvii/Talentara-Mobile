package com.example.talentara.data.model.response.notification

import com.google.gson.annotations.SerializedName

data class NotificationUpdateResponse(

	@field:SerializedName("updatedNotification")
	val updatedNotification: UpdatedNotification? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class UpdatedNotification(

	@field:SerializedName("notification_desc")
	val notificationDesc: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("notification_title")
	val notificationTitle: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("notification_id")
	val notificationId: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)
