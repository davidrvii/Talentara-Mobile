package com.example.talentara.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.talentara.data.local.preference.UserPreference
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
import com.example.talentara.data.model.response.project.UpdateProjectResponse
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
import com.example.talentara.data.model.response.user.RegisterResponse
import com.example.talentara.data.model.response.user.UpdateUserResponse
import com.example.talentara.data.model.response.user.UserBasicResponse
import com.example.talentara.data.model.response.user.UserDetailResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.model.user.UserModel
import com.example.talentara.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
    ): RegisterResponse {
        return apiService.register(username, email, password)
    }

    suspend fun login(
        email: String,
        password: String,
    ) {
        val response = apiService.login(email, password)

        if (response.loginResult != null) {
            val userId = response.loginResult.userId
            val userToken = response.loginResult.token
            val userEmail = response.loginResult.userEmail
            val userName = response.loginResult.userName

            userPreference.saveUser(userId, userToken, userEmail, userName)
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getUserBasic(
        token: String,
        id: Int,
    ): LiveData<Results<UserBasicResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getUserBasic("Bearer $token", id)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getUserDetail(
        token: String,
        id: Int,
    ): LiveData<Results<UserDetailResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getUserDetail("Bearer $token", id)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateUser(
        token: String,
        id: Int,
        fields: Map<String, RequestBody>,
        userImage: MultipartBody.Part?,
        //fcmToken: String,
    ): LiveData<Results<UpdateUserResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updateUser(
                "Bearer $token",
                id,
                fields,
                userImage,
                //fcmToken
            )
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    //updateUserFCMToken

    fun updateUserIsOnProject(
        token: String,
        userId: Int,
        isOnProject: Boolean,
    ): LiveData<Results<UpdateUserResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updateUserIsOnProject("Bearer $token", userId, isOnProject)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateUserTalentAccess(
        token: String,
        userId: Int,
        talentAccess: Boolean,
    ): LiveData<Results<UpdateUserResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updateUserTalentAccess("Bearer $token", userId, talentAccess)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun addNotification(
        token: String,
        userId: Int,
        title: String,
        desc: String,
    ): LiveData<Results<NewNotificationResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.addNotification(token, userId, title, desc)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getNotificationHistory(
        token: String,
        userId: Int,
    ): LiveData<Results<NotificationHistoryResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getNotificationHistory("Bearer $token", userId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateNotification(
        token: String,
        notificationId: Int,
        status: String,
    ): LiveData<Results<UpdateNotificationResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updateNotification("Bearer $token", notificationId, status)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun deleteNotification(
        token: String,
        notificationId: Int,
    ): LiveData<Results<DeleteNotificationResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.deleteNotification("Bearer $token", notificationId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun addTimeline(
        token: String,
        projectId: Int,
        projectPhase: String,
        startDate: String,
        endDate: String,
    ): LiveData<Results<NewTimelineResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.addTimeline(token, projectId, projectPhase, startDate, endDate)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getProjectTimeline(
        token: String,
        projectId: Int,
    ): LiveData<Results<TimelineProjectResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getProjectTimeline("Bearer $token", projectId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getTimelineDetail(
        token: String,
        timelineId: Int,
    ): LiveData<Results<TimelineDetailResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getTimelineDetail("Bearer $token", timelineId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getCurrentTimeline(
        token: String,
        projectId: Int,
    ): LiveData<Results<CurrentTimelineResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getCurrentTimeline("Bearer $token", projectId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getTimelineApprove(
        token: String,
        projectId: Int,
    ): LiveData<Results<TimelineApprovementResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getTimelineApprove("Bearer $token", projectId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTimeline(
        token: String,
        timelineId: Int,
        projectPhase: String,
        startDate: String,
        endDate: String,
        evidence: String,
    ): LiveData<Results<UpdateTimelineResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updateTimeline(
                "Bearer $token",
                timelineId,
                projectPhase,
                startDate,
                endDate,
                evidence
            )
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTimelineClientApprove(
        token: String,
        timelineId: Int,
        clientApproved: Boolean,
    ): LiveData<Results<UpdateTimelineResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.updateTimelineClientApprove("Bearer $token", timelineId, clientApproved)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTimelineLeaderApprove(
        token: String,
        timelineId: Int,
        leaderApproved: Boolean,
    ): LiveData<Results<UpdateTimelineResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.updateTimelineLeaderApprove("Bearer $token", timelineId, leaderApproved)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTimelineCompletedDate(
        token: String,
        timelineId: Int,
        completedDate: String,
    ): LiveData<Results<UpdateTimelineResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.updateTimelineCompletedDate("Bearer $token", timelineId, completedDate)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun deleteTimeline(
        token: String,
        timelineId: Int,
    ): LiveData<Results<DeleteTimelineResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.deleteTimeline("Bearer $token", timelineId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun addTalent(
        token: String,
        request: ApiService.AddTalentRequest,
    ): LiveData<Results<NewTalentResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.addTalent("Bearer $token", request)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getTalentDetail(
        token: String,
        talentId: Int,
    ): LiveData<Results<TalentDetailResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getTalentDetail("Bearer $token", talentId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTalent(
        token: String,
        talentId: Int,
        request: ApiService.UpdateTalentRequest,
    ): LiveData<Results<UpdateTalentResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updateTalent("Bearer $token", talentId, request)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTalentIsOnProject(
        token: String,
        talentId: Int,
        isOnProject: Boolean,
    ): LiveData<Results<UpdateTalentResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.updateTalentIsOnProject("Bearer $token", talentId, isOnProject)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTalentIsProjectManager(
        token: String,
        talentId: Int,
        isProjectManager: Boolean,
    ): LiveData<Results<UpdateTalentResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.updateTalentIsProjectManager("Bearer $token", talentId, isProjectManager)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTalentProjectDone(
        token: String,
        talentId: Int,
        projectDone: Int,
    ): LiveData<Results<UpdateTalentResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.updateTalentProjectDone("Bearer $token", talentId, projectDone)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateTalentAvailability(
        token: String,
        talentId: Int,
        availability: Boolean,
    ): LiveData<Results<UpdateTalentResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.updateTalentAvailability("Bearer $token", talentId, availability)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun addPortfolio(
        token: String,
        request: ApiService.AddPortfolioRequest,
    ): LiveData<Results<NewPortfolioResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.addPortfolio("Bearer $token", request)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getTalentPortfolio(
        token: String,
        talentId: Int,
    ): LiveData<Results<PortfolioTalentResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getTalentPortfolio("Bearer $token", talentId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getPortfolioDetail(
        token: String,
        portfolioId: Int,
    ): LiveData<Results<PortfolioDetailResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getPortfolioDetail("Bearer $token", portfolioId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updatePortfolio(
        token: String,
        portfolioId: Int,
        request: ApiService.UpdatePortfolioRequest,
    ): LiveData<Results<UpdatePortfolioResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updatePortfolio("Bearer $token", portfolioId, request)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun deletePortfolio(
        token: String,
        portfolioId: Int,
    ): LiveData<Results<PortfolioDeleteResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.deletePortfolio("Bearer $token", portfolioId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun addProject(
        token: String,
        statusId: Int,
        clientName: String,
        projectName: String,
        projectDesc: String,
        startDate: String,
        endDate: String,
    ): LiveData<Results<NewProjectResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.addProject(
                "Bearer $token",
                statusId,
                clientName,
                projectName,
                projectDesc,
                startDate,
                endDate
            )
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getProjectDetail(
        token: String,
        projectId: Int,
    ): LiveData<Results<ProjectDetailResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getProjectDetail("Bearer $token", projectId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getProjectHistory(
        token: String,
        userId: Int,
    ): LiveData<Results<ProjectHistoryResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getProjectHistory("Bearer $token", userId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getCurrentProject(
        token: String,
        userId: Int,
    ): LiveData<Results<CurrentProjectResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getCurrentProject("Bearer $token", userId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getProjectAccess(
        token: String,
        projectId: Int,
    ): LiveData<Results<ProjectAccessResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getProjectAccess("Bearer $token", projectId)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateProject(
        token: String,
        projectId: Int,
        request: ApiService.UpdateProjectRequest,
    ): LiveData<Results<UpdateProjectResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updateProject("Bearer $token", projectId, request)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun updateProjectCompleted(
        token: String,
        projectId: Int,
        statusId: Int,
        completedDate: String,
    ): LiveData<Results<UpdateProjectResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.updateProjectCompleted(
                "Bearer $token",
                projectId,
                statusId,
                completedDate
            )
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun projectOffer(
        token: String,
        projectId: Int,
        talentId: Int,
        roleName: String,
        accept: Boolean,
    ): LiveData<Results<ProjectOfferResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response =
                apiService.projectOffer("Bearer $token", projectId, talentId, roleName, accept)
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    //Project Order

    fun getAllCategories(
        token: String,
    ): LiveData<Results<GetAllCategoriesResponse>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getAllCategories("Bearer $token")
            emit(Results.Success(response))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService)
            }.also { instance = it }
    }
}