package com.example.talentara.data.model.response.user

import com.google.gson.annotations.SerializedName

data class SaveFcmTokenResponse(

	@field:SerializedName("fcmToken")
	val fcmToken: String? = null
)
