package com.example.talentara.data.model.response.talent

import com.google.gson.annotations.SerializedName

data class NewTalentResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("newTalent")
	val newTalent: NewTalent? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class PortfolioItem(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("portfolio_github")
	val portfolioGithub: String? = null,

	@field:SerializedName("languages")
	val languages: List<String?>? = null,

	@field:SerializedName("product_types")
	val productTypes: List<String?>? = null,

	@field:SerializedName("roles")
	val roles: List<String?>? = null,

	@field:SerializedName("portfolio_name")
	val portfolioName: String? = null,

	@field:SerializedName("portfolio_desc")
	val portfolioDesc: String? = null,

	@field:SerializedName("tools")
	val tools: List<String?>? = null,

	@field:SerializedName("portfolio_linkedin")
	val portfolioLinkedin: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("platforms")
	val platforms: List<String?>? = null
)

data class NewTalent(

	@field:SerializedName("product_types")
	val productTypes: List<String?>? = null,

	@field:SerializedName("languages")
	val languages: List<String?>? = null,

	@field:SerializedName("portfolio")
	val portfolio: List<PortfolioItem?>? = null,

	@field:SerializedName("roles")
	val roles: List<String?>? = null,

	@field:SerializedName("tools")
	val tools: List<String?>? = null,

	@field:SerializedName("platforms")
	val platforms: List<String?>? = null
)
