package com.example.talentara.data.remote

import com.example.talentara.data.model.response.categories.GetAllCategoriesResponse
import com.example.talentara.data.model.response.notification.DeleteNotificationResponse
import com.example.talentara.data.model.response.notification.NewNotificationResponse
import com.example.talentara.data.model.response.notification.NotificationHistoryResponse
import com.example.talentara.data.model.response.notification.UpdateNotificationResponse
import com.example.talentara.data.model.response.portfolio.NewPortfolioResponse
import com.example.talentara.data.model.response.portfolio.PortfolioDeleteResponse
import com.example.talentara.data.model.response.portfolio.PortfolioDetailResponse
import com.example.talentara.data.model.response.portfolio.PortfolioTalentResponse
import com.example.talentara.data.model.response.portfolio.UpdatePortfolioResponse
import com.example.talentara.data.model.response.project.CurrentProjectResponse
import com.example.talentara.data.model.response.project.NewProjectResponse
import com.example.talentara.data.model.response.project.ProjectAccessResponse
import com.example.talentara.data.model.response.project.ProjectDetailResponse
import com.example.talentara.data.model.response.project.ProjectHistoryResponse
import com.example.talentara.data.model.response.project.ProjectOfferResponse
import com.example.talentara.data.model.response.project.ProjectOrderResponse
import com.example.talentara.data.model.response.project.UpdateProjectResponse
import com.example.talentara.data.model.response.project.UpdateProjectStatusResponse
import com.example.talentara.data.model.response.talent.NewTalentResponse
import com.example.talentara.data.model.response.talent.TalentDetailResponse
import com.example.talentara.data.model.response.talent.UpdateTalentResponse
import com.example.talentara.data.model.response.timeline.CurrentTimelineResponse
import com.example.talentara.data.model.response.timeline.DeleteTimelineResponse
import com.example.talentara.data.model.response.timeline.NewTimelineResponse
import com.example.talentara.data.model.response.timeline.TimelineApprovementResponse
import com.example.talentara.data.model.response.timeline.TimelineDetailResponse
import com.example.talentara.data.model.response.timeline.TimelineProjectResponse
import com.example.talentara.data.model.response.timeline.UpdateTimelineResponse
import com.example.talentara.data.model.response.user.LoginResponse
import com.example.talentara.data.model.response.user.RegisterResponse
import com.example.talentara.data.model.response.user.SaveFcmTokenResponse
import com.example.talentara.data.model.response.user.UpdateUserResponse
import com.example.talentara.data.model.response.user.UserBasicResponse
import com.example.talentara.data.model.response.user.UserDetailResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
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

    @GET("user/basic/{user_id}")
    suspend fun getUserBasic(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
    ): UserBasicResponse

    @GET("user/detail/{user_id}")
    suspend fun getUserDetail(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
    ): UserDetailResponse

    @Multipart
    @PATCH("user/update/{user_id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part userImage: MultipartBody.Part? = null,
    ): UpdateUserResponse

    @FormUrlEncoded
    @PATCH("user/fcm")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Field("fcm_token") fcmToken: String,
    ): SaveFcmTokenResponse

    @FormUrlEncoded
    @PATCH("user/update/{user_id}")
    suspend fun updateUserIsOnProject(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @Field("is_on_project") isOnProject: Int,
    ): UpdateUserResponse

    @FormUrlEncoded
    @PATCH("user/update/{user_id}")
    suspend fun updateUserTalentAccess(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @Field("talent_access") talentAccess: Int,
    ): UpdateUserResponse

    //NOTIFICATION
    @FormUrlEncoded
    @POST("notification/add")
    suspend fun addNotification(
        @Header("Authorization") token: String,
        @Field("user_id") userId: Int,
        @Field("notification_title") title: String,
        @Field("notification_desc") desc: String,
        @Field("notification_type") type: String,
        @Field("click_action") clickAction: String,
    ): NewNotificationResponse

    @GET("notification/history/{user_id}")
    suspend fun getNotificationHistory(
        @Header("Authorization") token: String,
        @Path("user_id") userId: Int,
    ): NotificationHistoryResponse

    @FormUrlEncoded
    @PATCH("notification/update/{notification_id}")
    suspend fun updateNotification(
        @Header("Authorization") token: String,
        @Path("notification_id") notificationId: Int,
        @Field("status") status: String,
    ): UpdateNotificationResponse

    @DELETE("notification/delete/{notification_id}")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Path("notification_id") notificationId: Int,
    ): DeleteNotificationResponse

    //TIMELINE
    @FormUrlEncoded
    @POST("timeline/add")
    suspend fun addTimeline(
        @Header("Authorization") token: String,
        @Field("project_id") projectId: Int,
        @Field("project_phase") projectPhase: String,
        @Field("start_date") startDate: String,
        @Field("end_date") endDate: String,
    ): NewTimelineResponse

    @GET("timeline/project/{project_id}")
    suspend fun getProjectTimeline(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): TimelineProjectResponse

    @GET("timeline/detail/{timeline_id}")
    suspend fun getTimelineDetail(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
    ): TimelineDetailResponse

    @GET("timeline/current/{project_id}")
    suspend fun getCurrentTimeline(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): CurrentTimelineResponse

    @GET("timeline/approve/{project_id}")
    suspend fun getTimelineApprove(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): TimelineApprovementResponse

    @FormUrlEncoded
    @PATCH("timeline/update/{timeline_id}")
    suspend fun updateTimeline(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("project_phase") projectPhase: String,
        @Field("start_date") startDate: String,
        @Field("end_date") endDate: String,
        @Field("evidance") evidence: String,
    ): UpdateTimelineResponse

    @FormUrlEncoded
    @PATCH("timeline/update/{timeline_id}")
    suspend fun updateTimelineClientApprove(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("client_approved") clientApproved: Int,
    ): UpdateTimelineResponse

    @FormUrlEncoded
    @PATCH("timeline/update/{timeline_id}")
    suspend fun updateTimelineLeaderApprove(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("leader_approved") leaderApproved: Int,
    ): UpdateTimelineResponse

    @FormUrlEncoded
    @PATCH("timeline/update/{timeline_id}")
    suspend fun updateTimelineCompletedDate(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("completed_date") completedDate: String,
    ): UpdateTimelineResponse

    @DELETE("timeline/delete/{timeline_id}")
    suspend fun deleteTimeline(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
    ): DeleteTimelineResponse

    //TALENT
    @POST("talent/add")
    suspend fun addTalent(
        @Header("Authorization") token: String,
        @Body request: AddTalentRequest,
    ): NewTalentResponse

    data class AddTalentRequest(
        val github: String,
        val linkedIn: String,
        val roles: List<String>,
        val tools: List<String>,
        val platforms: List<String>,
        @SerializedName("product_types")
        val productTypes: List<String>,
        val languages: List<String>,
        val portfolio: List<PortfolioItem>,
    )

    data class PortfolioItem(
        @SerializedName("client_name")
        val clientName: String,
        @SerializedName("portfolio_name")
        val portfolioName: String,
        @SerializedName("portfolio_linkedin")
        val linkedin: String,
        @SerializedName("portfolio_github")
        val github: String,
        @SerializedName("portfolio_desc")
        val description: String,
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
        val features: List<String>,
    )

    @GET("talent/detail/{talent_id}")
    suspend fun getTalentDetail(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
    ): TalentDetailResponse

    @PATCH("talent/update/{talent_id}")
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

    @FormUrlEncoded
    @PATCH("talent/update/{talent_id}")
    suspend fun updateTalentProjectDeclined(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("project_declined") projectDeclined: Int,
    ): UpdateTalentResponse

    @FormUrlEncoded
    @PATCH("talent/update/{talent_id}")
    suspend fun updateTalentIsProjectManager(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("is_project_manager") isProjectManager: Int,
    ): UpdateTalentResponse

    @FormUrlEncoded
    @PATCH("talent/update/{talent_id}")
    suspend fun updateTalentProjectDone(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("project_done") projectDone: Int,
    ): UpdateTalentResponse

    @FormUrlEncoded
    @PATCH("talent/update/{talent_id}")
    suspend fun updateTalentAvailability(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
        @Field("availability") availability: Int,
    ): UpdateTalentResponse

    //PORTFOLIO
    @POST("portfolio/add")
    suspend fun addPortfolio(
        @Header("Authorization") token: String,
        @Body request: AddPortfolioRequest,
    ): NewPortfolioResponse

    data class AddPortfolioRequest(
        @SerializedName("talent_id")
        val talentId: Int,
        @SerializedName("client_name")
        val clientName: String,
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
        val feature: List<String>,
    )

    @GET("portfolio/talent/{talent_id}")
    suspend fun getTalentPortfolio(
        @Header("Authorization") token: String,
        @Path("talent_id") talentId: Int,
    ): PortfolioTalentResponse

    @GET("portfolio/detail/{portfolio_id}")
    suspend fun getPortfolioDetail(
        @Header("Authorization") token: String,
        @Path("portfolio_id") portfolioId: Int,
    ): PortfolioDetailResponse

    @PATCH("portfolio/update/{portfolio_id}")
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

    @DELETE("portfolio/delete/{portfolio_id}")
    suspend fun deletePortfolio(
        @Header("Authorization") token: String,
        @Path("portfolio_id") portfolioId: Int,
    ): PortfolioDeleteResponse

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

    @GET("project/detail/{project_id}")
    suspend fun getProjectDetail(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): ProjectDetailResponse

    @GET("project/order/{project_id}")
    suspend fun getProjectOrder(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): ProjectOrderResponse

    @GET("project/history/{user_id}")
    suspend fun getProjectHistory(
        @Header("Authorization") token: String,
        @Path("user_id") userId: Int,
    ): ProjectHistoryResponse

    @GET("project/current/{user_id}")
    suspend fun getCurrentProject(
        @Header("Authorization") token: String,
        @Path("user_id") userId: Int,
    ): CurrentProjectResponse

    @GET("project/access/{project_id}")
    suspend fun getProjectAccess(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
    ): ProjectAccessResponse

    @PATCH("project/update/{project_id}")
    suspend fun updateProject(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
        @Body request: UpdateProjectRequest,
    ): UpdateProjectResponse

    data class UpdateProjectRequest(
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
        @SerializedName("project_github")
        val projectGithub: String,
        @SerializedName("meet_link")
        val meetLink: String,
    )

    data class ProjectRole(
        @SerializedName("role_name")
        val roleName: String,
        val amount: Int,
    )

    @FormUrlEncoded
    @PATCH("project/status/{project_id}")
    suspend fun updateProjectStatus(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
        @Field("status_id") statusId: Int,
    ): UpdateProjectStatusResponse


    @FormUrlEncoded
    @PATCH("project/completed/{project_id}")
    suspend fun updateProjectCompleted(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: Int,
        @Field("status_id") statusId: Int,
        @Field("completed_date") completedDate: String,
    ): UpdateProjectResponse

    @FormUrlEncoded
    @PATCH("project/offer")
    suspend fun projectOffer(
        @Header("Authorization") token: String,
        @Field("project_id") projectId: Int,
        @Field("talent_id") talentId: Int,
        @Field("role_name") roleName: String,
        @Field("accept") accept: Int,
    ): ProjectOfferResponse

    //PROJECT ORDER

    @GET("categories/all")
    suspend fun getAllCategories(
        @Header("Authorization") token: String,
    ): GetAllCategoriesResponse
}