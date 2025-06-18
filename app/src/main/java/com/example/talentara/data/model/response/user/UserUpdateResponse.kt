package com.example.talentara.data.model.response.user

import com.google.gson.annotations.SerializedName

data class UserUpdateResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("updatedUser")
	val updatedUser: UpdatedUser? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class UpdatedUser(

	@field:SerializedName("user_email")
	val userEmail: String? = null,

	@field:SerializedName("user_password")
	val userPassword: String? = null,

	@field:SerializedName("github")
	val github: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("user_image")
	val userImage: Any? = null,

	@field:SerializedName("user_name")
	val userName: String? = null,

	@field:SerializedName("is_on_project")
	val isOnProject: Int? = null,

	@field:SerializedName("fcm_token")
	val fcmToken: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("linkedin")
	val linkedin: Any? = null,

	@field:SerializedName("talent_access")
	val talentAccess: Int? = null
)
