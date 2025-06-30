package com.example.talentara.data.model.response.portfolio

import com.google.gson.annotations.SerializedName

data class PortfolioDeleteResponse(

	@field:SerializedName("deletedPortfolioId")
	val deletedPortfolioId: String? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
