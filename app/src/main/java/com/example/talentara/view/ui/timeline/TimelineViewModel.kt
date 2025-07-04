package com.example.talentara.view.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.project.ProjectAccessResponse
import com.example.talentara.data.model.response.project.UpdateProjectResponse
import com.example.talentara.data.model.response.talent.UpdateTalentResponse
import com.example.talentara.data.model.response.timeline.DeleteTimelineResponse
import com.example.talentara.data.model.response.timeline.NewTimelineResponse
import com.example.talentara.data.model.response.timeline.TimelineApprovementResponse
import com.example.talentara.data.model.response.timeline.TimelineDetailResponse
import com.example.talentara.data.model.response.timeline.TimelineProjectResponse
import com.example.talentara.data.model.response.timeline.UpdateTimelineResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TimelineViewModel(private val repository: Repository) : ViewModel() {

    private val _getProjectTimeline = MediatorLiveData<Results<TimelineProjectResponse>>()
    val getProjectTimeline: LiveData<Results<TimelineProjectResponse>> = _getProjectTimeline

    fun getProjectTimeline(
        projectId: Int,
    ) {
        viewModelScope.launch {
            _getProjectTimeline.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _getProjectTimeline.addSource(
                    repository.getProjectTimeline(
                        token,
                        projectId
                    )
                ) { result ->
                    _getProjectTimeline.value = result
                }
            } catch (e: Exception) {
                _getProjectTimeline.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getTimelineDetail = MediatorLiveData<Results<TimelineDetailResponse>>()
    val getTimelineDetail: LiveData<Results<TimelineDetailResponse>> = _getTimelineDetail

    fun getTimelineDetail(
        timelineId: Int,
    ) {
        viewModelScope.launch {
            _getTimelineDetail.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _getTimelineDetail.addSource(
                    repository.getTimelineDetail(
                        token,
                        timelineId
                        )
                ) { result ->
                    _getTimelineDetail.value = result
                }
            } catch (e: Exception) {
                _getTimelineDetail.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTimeline = MediatorLiveData<Results<UpdateTimelineResponse>>()
    val updateTimeline: LiveData<Results<UpdateTimelineResponse>> = _updateTimeline

    fun updateTimeline(
        timelineId: Int,
        projectPhase: String,
        startDate: String,
        endDate: String,
        evidence: String
    ) {
        viewModelScope.launch {
            _updateTimeline.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _updateTimeline.addSource(
                    repository.updateTimeline(
                        token,
                        timelineId,
                        projectPhase,
                        startDate,
                        endDate,
                        evidence
                    )
                ) { result ->
                    _updateTimeline.value = result
                }
            } catch (e: Exception) {
                _updateTimeline.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _addTimeline = MediatorLiveData<Results<NewTimelineResponse>>()
    val addTimeline: LiveData<Results<NewTimelineResponse>> = _addTimeline

    fun addTimeline(
        projectId: Int,
        projectPhase: String,
        startDate: String,
        endDate: String,
    ) {
        viewModelScope.launch {
            _addTimeline.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _addTimeline.addSource(
                    repository.addTimeline(
                        token,
                        projectId,
                        projectPhase,
                        startDate,
                        endDate
                    )
                ) { result ->
                    _addTimeline.value = result
                }
            } catch (e: Exception) {
                _addTimeline.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getProjectAccess = MediatorLiveData<Results<ProjectAccessResponse>>()
    val getProjectAccess: LiveData<Results<ProjectAccessResponse>> = _getProjectAccess

    fun getProjectAccess(
        projectId: Int,
    ) {
        viewModelScope.launch {
            _getProjectAccess.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _getProjectAccess.addSource(
                    repository.getProjectAccess(
                        token,
                        projectId
                        )
                ) { result ->
                    _getProjectAccess.value = result
                }
            } catch (e: Exception) {
                _getProjectAccess.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getTimelineApprove = MediatorLiveData<Results<TimelineApprovementResponse>>()
    val getTimelineApprove: LiveData<Results<TimelineApprovementResponse>> = _getTimelineApprove

    fun getTimelineApprove(
        projectId: Int,
    ) {
        viewModelScope.launch {
            _getTimelineApprove.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _getTimelineApprove.addSource(
                    repository.getTimelineApprove(
                        token,
                        projectId
                    )
                    ) { result ->
                    _getTimelineApprove.value = result
                }
            } catch (e: Exception) {
                _getTimelineApprove.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTimelineClientApprove = MediatorLiveData<Results<UpdateTimelineResponse>>()
    val updateTimelineClientApprove: LiveData<Results<UpdateTimelineResponse>> =
        _updateTimelineClientApprove

    fun updateTimelineClientApprove(
        timelineId: Int,
        clientApproved: Int,
    ) {
        viewModelScope.launch {
            _updateTimelineClientApprove.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _updateTimelineClientApprove.addSource(
                    repository.updateTimelineClientApprove(
                        token,
                        timelineId,
                        clientApproved
                    )
                ) { result ->
                    _updateTimelineClientApprove.value = result
                }
            } catch (e: Exception) {
                _updateTimelineClientApprove.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTimelineLeaderApprove = MediatorLiveData<Results<UpdateTimelineResponse>>()
    val updateTimelineLeaderApprove: LiveData<Results<UpdateTimelineResponse>> =
        _updateTimelineLeaderApprove

    fun updateTimelineLeaderApprove(
        timelineId: Int,
        leaderApproved: Int,
    ) {
        viewModelScope.launch {
            _updateTimelineLeaderApprove.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _updateTimelineLeaderApprove.addSource(
                    repository.updateTimelineLeaderApprove(
                        token,
                        timelineId,
                        leaderApproved
                    )
                ) { result ->
                    _updateTimelineLeaderApprove.value = result
                }
            } catch (e: Exception) {
                _updateTimelineLeaderApprove.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTimelineCompletedDate = MediatorLiveData<Results<UpdateTimelineResponse>>()
    val updateTimelineCompletedDate: LiveData<Results<UpdateTimelineResponse>> =
        _updateTimelineCompletedDate

    fun updateTimelineCompletedDate(
        timelineId: Int,
        completedDate: String,
    ) {
        viewModelScope.launch {
            _updateTimelineCompletedDate.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _updateTimelineCompletedDate.addSource(
                    repository.updateTimelineCompletedDate(
                        token,
                        timelineId,
                        completedDate
                    )
                ) { result ->
                    _updateTimelineCompletedDate.value = result
                }
            } catch (e: Exception) {
                _updateTimelineCompletedDate.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _deleteTimeline = MediatorLiveData<Results<DeleteTimelineResponse>>()
    val deleteTimeline: LiveData<Results<DeleteTimelineResponse>> = _deleteTimeline

    fun deleteTimeline(
        timelineId: Int,
    ) {
        viewModelScope.launch {
            _deleteTimeline.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _deleteTimeline.addSource(repository.deleteTimeline(token, timelineId)) { result ->
                    _deleteTimeline.value = result
                }
            } catch (e: Exception) {
                _deleteTimeline.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTalentProjectDone = MediatorLiveData<Results<UpdateTalentResponse>>()
    val updateTalentProjectDone: LiveData<Results<UpdateTalentResponse>> = _updateTalentProjectDone

    fun updateTalentProjectDone(
        projectDone: Int,
        talentId: Int
    ) {
        viewModelScope.launch {
            _updateTalentProjectDone.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _updateTalentProjectDone.addSource(
                    repository.updateTalentProjectDone(
                        token,
                        talentId,
                        projectDone
                    )
                ) { result ->
                    _updateTalentProjectDone.value = result
                }
            } catch (e: Exception) {
                _updateTalentProjectDone.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateProjectCompleted = MediatorLiveData<Results<UpdateProjectResponse>>()
    val updateProjectCompleted: LiveData<Results<UpdateProjectResponse>> = _updateProjectCompleted

    fun updateProjectCompleted(
        projectId: Int,
        projectCompleted: String,
    ) {
        viewModelScope.launch {
            _updateProjectCompleted.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val statusId = 4

                _updateProjectCompleted.addSource(
                    repository.updateProjectCompleted(
                        token,
                        projectId,
                        statusId,
                        projectCompleted
                        )
                ) { result ->
                    _updateProjectCompleted.value = result
                }
            } catch (e: Exception) {
                _updateProjectCompleted.value = Results.Error(e.message.toString())
            }
        }
    }
}
