package com.example.talentara.view.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.project.CurrentProjectResponse
import com.example.talentara.data.model.response.timeline.CurrentTimelineResponse
import com.example.talentara.data.model.response.user.UserBasicResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _getUserBasic = MediatorLiveData<Results<UserBasicResponse>>()
    val getUserBasic: LiveData<Results<UserBasicResponse>> = _getUserBasic

    fun getUserBasic() {
        viewModelScope.launch {
            _getUserBasic.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId

                _getUserBasic.addSource(repository.getUserBasic(token, userId)) { result ->
                    _getUserBasic.value = result
                }

            } catch (e: Exception) {
                _getUserBasic.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getCurrentProject = MediatorLiveData<Results<CurrentProjectResponse>>()
    val getCurrentProject: LiveData<Results<CurrentProjectResponse>> = _getCurrentProject

    fun getCurrentProject() {
        viewModelScope.launch {
            _getCurrentProject.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId

                _getCurrentProject.addSource(
                    repository.getCurrentProject(
                        token,
                        userId
                    )
                ) { result ->
                    _getCurrentProject.value = result
                }

            } catch (e: Exception) {
                _getCurrentProject.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getCurrentTimeline = MediatorLiveData<Results<CurrentTimelineResponse>>()
    val getCurrentTimeline: LiveData<Results<CurrentTimelineResponse>> = _getCurrentTimeline

    fun getCurrentTimeline(
        projectId: Int,
    ) {
        viewModelScope.launch {
            _getCurrentTimeline.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                _getCurrentTimeline.addSource(
                    repository.getCurrentTimeline(
                        token,
                        projectId
                    )
                ) { result ->
                    _getCurrentTimeline.value = result
                }
            } catch (e: Exception) {
                _getCurrentTimeline.value = Results.Error(e.message.toString())
            }
        }
    }
}