package com.example.talentara.data.model.response.portfolio

import com.google.gson.annotations.SerializedName

data class PortfolioDetailResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("portfolioDetail")
	val portfolioDetail: List<PortfolioDetailItem?>? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class PortfolioDetailItem(

	@field:SerializedName("client_name")
	val clientName: String? = null,

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("languages")
	val languages: String? = null,

	@field:SerializedName("product_types")
	val productTypes: String? = null,

	@field:SerializedName("roles")
	val roles: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("portfolio_name")
	val portfolioName: String? = null,

	@field:SerializedName("portfolio_desc")
	val portfolioDesc: String? = null,

	@field:SerializedName("tools")
	val tools: String? = null,

	@field:SerializedName("portfolio_linkedin")
	val portfolioLinkedin: String? = null,

	@field:SerializedName("platforms")
	val platforms: String? = null,

	@field:SerializedName("features")
	val features: String? = null,

	@field:SerializedName("portfolio_github")
	val portfolioGithub: String? = null,

	@field:SerializedName("portfolio_id")
	val portfolioId: Int? = null,

	@field:SerializedName("portfolio_label")
	val portfolioLabel: String? = null,

	@field:SerializedName("talent_id")
	val talentId: Int? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
)
