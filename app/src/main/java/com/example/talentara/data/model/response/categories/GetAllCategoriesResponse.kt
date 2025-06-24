package com.example.talentara.data.model.response.categories

import com.google.gson.annotations.SerializedName

data class GetAllCategoriesResponse(

	@field:SerializedName("role")
	val role: List<RoleItem?>? = null,

	@field:SerializedName("feature")
	val feature: List<FeatureItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("language")
	val language: List<LanguageItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("tools")
	val tools: List<ToolsItem?>? = null,

	@field:SerializedName("platform")
	val platform: List<PlatformItem?>? = null,

	@field:SerializedName("productType")
	val productType: List<ProductTypeItem?>? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class FeatureItem(

	@field:SerializedName("feature_name")
	val featureName: String? = null
)

data class ToolsItem(

	@field:SerializedName("tools_name")
	val toolsName: String? = null
)

data class LanguageItem(

	@field:SerializedName("language_name")
	val languageName: String? = null
)

data class PlatformItem(

	@field:SerializedName("platform_name")
	val platformName: String? = null
)

data class ProductTypeItem(

	@field:SerializedName("product_type_name")
	val productTypeName: String? = null
)

data class RoleItem(

	@field:SerializedName("role_name")
	val roleName: String? = null
)
