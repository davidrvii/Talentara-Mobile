package com.example.talentara.data.remote

import com.example.talentara.data.model.response.notification.NewNotificationResponse
import com.example.talentara.data.model.response.notification.DeleteNotificationResponse
import com.example.talentara.data.model.response.notification.NotificationHistoryResponse
import com.example.talentara.data.model.response.notification.UpdateNotificationResponse
import com.example.talentara.data.model.response.timeline.CurrentTimelineResponse
import com.example.talentara.data.model.response.timeline.DeleteTimelineResponse
import com.example.talentara.data.model.response.timeline.NewTimelineResponse
import com.example.talentara.data.model.response.timeline.TimelineDetailResponse
import com.example.talentara.data.model.response.timeline.TimelineProjectResponse
import com.example.talentara.data.model.response.timeline.UpdateTimelineResponse
import com.example.talentara.data.model.response.user.LoginResponse
import com.example.talentara.data.model.response.user.RegisterResponse
import com.example.talentara.data.model.response.user.UserBasicResponse
import com.example.talentara.data.model.response.user.DeleteUserResponse
import com.example.talentara.data.model.response.user.UpdateUserResponse
import com.example.talentara.data.model.response.user.UserDetailResponse
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

    //Seperate or Create Multiple updateUser based on Function Needs
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
        @Field("is_on_project") isOnProject: Int,
        @Field("talent_access") talentAccess: Int,
        @Field("user_image") userImage: String,
        @Field("fcm_token") fcmToken: String,
    ): UpdateUserResponse

    @DELETE("user/delete/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): DeleteUserResponse

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

    @GET("timeline/detail/{id}")
    suspend fun getTimelineDetail(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
    ): TimelineDetailResponse

    @PATCH("timeline/update/{id}")
    suspend fun updateTimeline(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
        @Field("project_phase") projectPhase: String,
        @Field("start_date") startDate: String,
        @Field("end_date") endDate: String,
        @Field("evidance") evidance: String,
        @Field("client_approved") clientApproved: String,
        @Field("leader_approved") leaderApproved: String,
        @Field("completed_date") completedDate: String,
    ): UpdateTimelineResponse

    @DELETE("timeline/delete/{id}")
    suspend fun deleteTimeline(
        @Header("Authorization") token: String,
        @Path("timeline_id") timelineId: Int,
    ): DeleteTimelineResponse
}