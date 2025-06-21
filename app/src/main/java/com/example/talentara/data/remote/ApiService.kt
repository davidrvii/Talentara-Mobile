package com.example.talentara.data.remote

import com.example.talentara.data.model.response.notification.DeleteNotificationResponse
import com.example.talentara.data.model.response.notification.NewNotificationResponse
import com.example.talentara.data.model.response.notification.NotificationHistoryResponse
import com.example.talentara.data.model.response.notification.UpdateNotificationResponse
import com.example.talentara.data.model.response.portfolio.NewPortfolioResponse
import com.example.talentara.data.model.response.portfolio.PortfolioDetailResponse
import com.example.talentara.data.model.response.portfolio.PortfolioTalentResponse
import com.example.talentara.data.model.response.portfolio.UpdatePortfolioResponse
import com.example.talentara.data.model.response.project.NewProjectResponse
import com.example.talentara.data.model.response.project.ProjectDetailResponse
import com.example.talentara.data.model.response.project.ProjectHistoryResponse
import com.example.talentara.data.model.response.project.ProjectOfferResponse
import com.example.talentara.data.model.response.project.UpdateProjectResponse
import com.example.talentara.data.model.response.talent.NewTalentResponse
import com.example.talentara.data.model.response.talent.TalentDetailResponse
import com.example.talentara.data.model.response.talent.UpdateTalentResponse
import com.example.talentara.data.model.response.timeline.CurrentTimelineResponse
import com.example.talentara.data.model.response.timeline.DeleteTimelineResponse
import com.example.talentara.data.model.response.timeline.NewTimelineResponse
import com.example.talentara.data.model.response.timeline.TimelineProjectResponse
import com.example.talentara.data.model.response.timeline.UpdateTimelineResponse
import com.example.talentara.data.model.response.user.LoginResponse
import com.example.talentara.data.model.response.user.RegisterResponse
import com.example.talentara.data.model.response.user.UpdateUserResponse
import com.example.talentara.data.model.response.user.UserBasicResponse
import com.example.talentara.data.model.response.user.UserDetailResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    //USER
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("user_name") username: String,
        @Field("user_email") email: String,
        @Field("user_password") password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("user_email") email: String,
        @Field("user_password") password: String,
    ): LoginResponse

    @GET("user/basic/{id}")
    suspend fun getUserBasic(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
    ): UserBasicResponse

    @GET("user/detail/{id}")
    suspend fun getUserDetail(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
    ): UserDetailResponse

    @FormUrlEncoded
    @PATCH("user/update/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @Field("user_name") username: String,
        @Field("user_email") email: String,
        @Field("user_password") password: String,
        @Field("github") github: String,
        @Field("linkedin") linkedin: String,
        @Field("user_image") userImage: String,
        @Field("fcm_token") fcmToken: String,
    ): UpdateUserResponse

    @FormUrlEncoded
    @PATCH("user/update/{id}")
    suspend fun updateUserIsOnProject(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @Field("is_on_project") isOnProject: Boolean,
    ): UpdateUserResponse

    @FormUrlEncoded
    @PATCH("user/update/{id}")
    suspend fun updateUserTalentAccess(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @Field("talent_access") talentAccess: Boolean,
    ): UpdateUserResponse

    //NOTIFICATION
    @FormUrlEncoded
    @POST("notification/add")
    suspend fun addNotification(
        @Field("user_id") userId: Int,
        @Field("notification_title") title: String,
        @Field("notification_desc") desc: String,
    ): NewNotificationResponse

    @GET("notification/history/{id}")
    suspend fun getNotificationHistory(
        @Header("Authorization") token: String,
        @Path("user_id") userId: Int,
    ): NotificationHistoryResponse

    @PATCH("notification/update/{id}")
    suspend fun updateNotification(
        @Header("Authorization") token: String,
        @Path("notification_id") notificationId: Int,
        @Field("status") status: String,
    ): UpdateNotificationResponse

    @DELETE("notification/delete/{id}")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Path("notification_id") notificationId: Int,
    ): DeleteNotificationResponse

    //TIMELINE
    @FormUrlEncoded
    @POST("timeline/add")
    suspend fun addTimeline(
        @Field("project_id") projectId: Int,
        @Field("project_phase") projectPhase: String,
        @Field("start_date") startDate: String,
        @Field("end_date") endDate: String,
    ): NewTimelineResponse

    @GET("timeline/project/{id}")
    suspend fun getProjectTimeline(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): TimelineProjectResponse

    @GET("timeline/current/{id}")
    suspend fun getCurrentTimeline(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): CurrentTimelineResponse

    @PATCH("timeline/update/{id}")
    suspend fun updateTimeline(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("project_phase") projectPhase: String,
        @Field("start_date") startDate: String,
        @Field("end_date") endDate: String,
    ): UpdateTimelineResponse

    @PATCH("timeline/update/{id}")
    suspend fun updateTimelineClientApprove(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("client_approved") clientApproved: String,
    ): UpdateTimelineResponse

    @PATCH("timeline/update/{id}")
    suspend fun updateTimelineLeaderApprove(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("leader_approved") leaderApproved: String,
    ): UpdateTimelineResponse

    @PATCH("timeline/update/{id}")
    suspend fun updateTimelineCompletedDate(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("completed_date") completedDate: String,
    ): UpdateTimelineResponse

    @PATCH("timeline/update/{id}")
    suspend fun updateTimelineEvidance(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("evidance") evidance: String,
    ): UpdateTimelineResponse

    @DELETE("timeline/delete/{id}")
    suspend fun deleteTimeline(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
    ): DeleteTimelineResponse

    //TALENT
    @FormUrlEncoded
    @POST("talent/add")
    suspend fun addTalent(
        @Header("Authorization") token: String,
        @Body request: AddTalentRequest,
    ): NewTalentResponse

    data class AddTalentRequest(
        val roles: List<String>,
        val tools: List<String>,
        val platforms: List<String>,
        @SerializedName("product_types")
        val productTypes: List<String>,
        val languages: List<String>,
        val portfolio: List<PortfolioItem>,
    )

    data class PortfolioItem(
        @SerializedName("portfolio_name")
        val name: String,
        @SerializedName("portfolio_linkedin")
        val linkedin: String,
        @SerializedName("portfolio_github")
        val github: String,
        @SerializedName("portfolio_desc")
        val description: String,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("end_date")
        val endDate: String,
        val platforms: List<String>,
        val tools: List<String>,
        val languages: List<String>,
        val roles: List<String>,
        @SerializedName("product_types")
        val productTypes: List<String>,
    )

    @GET("talent/detail/{id}")
    suspend fun getTalentDetail(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
    ): TalentDetailResponse

        @PATCH("talent/update/{id}")
    suspend fun updateTalent(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Body request: UpdateTalentRequest,
    ): UpdateTalentResponse

    data class UpdateTalentRequest(
        val roles: List<String>,
        val languages: List<String>,
        val tools: List<String>,
        @SerializedName("product_types")
        val productTypes: List<String>,
        val platforms: List<String>,
    )

    @PATCH("talent/update/{id}")
    suspend fun updateTalentIsOnProject(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("is_on_project") isOnProject: Boolean,
    ): UpdateTalentResponse

    @PATCH("talent/update/{id}")
    suspend fun updateTalentIsProjectManager(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("is_project_manager") isProjectManager: Boolean,
    ): UpdateTalentResponse

    @PATCH("talent/update/{id}")
    suspend fun updateTalentProjectDone(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("project_done") isProjectManager: Int,
    ): UpdateTalentResponse

    @PATCH("talent/update/{id}")
    suspend fun updateTalentAvailability(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("availability") availability: Int,
    ): UpdateTalentResponse

    @PATCH("talent/update/{id}")
    suspend fun updateTalentAvgRating(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("talent_avg_rating") talentAvgRating: Int,
    ): UpdateTalentResponse

    //PORTFOLIO
    @FormUrlEncoded
    @POST("portfolio/add")
    suspend fun addPortfolio(
        @Header("Authorization") token: String,
        @Body request: AddPortfolioRequest,
    ): NewPortfolioResponse

    data class AddPortfolioRequest(
        @SerializedName("portfolio_name")
        val portfolioName: String,
        @SerializedName("portfolio_linkedin")
        val portfolioLinkedin: String,
        @SerializedName("portfolio_github")
        val portfolioGithub: String,
        @SerializedName("portfolio_desc")
        val portfolioDesc: String,
        @SerializedName("portfolio_label")
        val portfolioLabel: String,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("end_date")
        val endDate: String,
        val platforms: List<String>,
        val tools: List<String>,
        val languages: List<String>,
        val roles: List<String>,
        @SerializedName("product_types")
        val productTypes: List<String>,
    )

    @GET("portfolio/talent/{id}")
    suspend fun getTalentPortfolio(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
    ): PortfolioTalentResponse

    @GET("portfolio/detail/{id}")
    suspend fun getPortfolioDetail(
        @Header("Authorization") token: String,
        @Path("portfolio_id") portfolioId: Int,
    ): PortfolioDetailResponse

    @PATCH("portfolio/update/{id}")
    suspend fun updatePortfolio(
        @Header("Authorization") token: String,
        @Path("portfolio_id") portfolioId: Int,
        @Body request: UpdatePortfolioRequest,
    ): UpdatePortfolioResponse

    data class UpdatePortfolioRequest(
        @SerializedName("portfolio_name")
        val portfolioName: String,
        @SerializedName("portfolio_linkedin")
        val portfolioLinkedin: String,
        @SerializedName("portfolio_github")
        val portfolioGithub: String,
        @SerializedName("portfolio_desc")
        val portfolioDesc: String,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("end_date")
        val endDate: String,
        val platforms: List<String>,
        val tools: List<String>,
        val languages: List<String>,
        val roles: List<String>,
        @SerializedName("product_types")
        val productTypes: List<String>,
    )

    //PROJECT
    @FormUrlEncoded
    @POST("project/add")
    suspend fun addProject(
        @Header("Authorization") token: String,
        @Field("status_id") statusId: Int,
        @Field("client_name") clientName: String,
        @Field("project_name") projectName: String,
        @Field("project_desc") projectDesc: String,
        @Field("start_date") startDate: String,
        @Field("end_date") endDate: String,
    ): NewProjectResponse

    @GET("project/detail/{id}")
    suspend fun getProjectDetail(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): ProjectDetailResponse

    @GET("project/history/{id}")
    suspend fun getProjectHistory(
        @Header("Authorization") token: String,
        @Path("user_id") userId: Int,
    ): ProjectHistoryResponse

    @PATCH("project/update/{id}")
    suspend fun updateProject(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
        @Body request: UpdateProjectRequest,
    ): UpdateProjectResponse

    data class UpdateProjectRequest(
        @SerializedName("status_id")
        val statusId: Int,
        @SerializedName("client_name")
        val clientName: String,
        @SerializedName("project_name")
        val projectName: String,
        @SerializedName("project_desc")
        val projectDesc: String,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("end_date")
        val endDate: String,
        val platform: List<String>,
        @SerializedName("product_type")
        val productType: List<String>,
        val role: List<ProjectRole>,
        val language: List<String>,
        val tools: List<String>,
        val feature: List<String>,
    )

    data class ProjectRole(
        @SerializedName("role_name")
        val roleName: String,
        val amount: Int,
    )

    @PATCH("project/offer")
    suspend fun projectOffer(
        @Header("Authorization") token: String,
        @Field("project_id") projectId: Int,
        @Field("talent_id") talentId: Int,
        @Field("role_name") roleName: String,
        @Field("accept") accept: String,
    ): ProjectOfferResponse

    //PROJECT ORDER
}