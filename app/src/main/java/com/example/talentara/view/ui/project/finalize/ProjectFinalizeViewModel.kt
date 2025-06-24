package com.example.talentara.view.ui.project.finalize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.project.UpdateProjectResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProjectFinalizeViewModel(private val repository: Repository) : ViewModel() {

    private val _updateProject = MediatorLiveData<Results<UpdateProjectResponse>>()
    val updateProject: LiveData<Results<UpdateProjectResponse>> = _updateProject

    fun updateProject(
        projectId: Int,
        request: ApiService.UpdateProjectRequest,
    ) {
        _updateProject.value = Results.Loading
        viewModelScope.launch {
            try {
                val token = repository.getSession().first().token

                _updateProject.addSource(repository.updateProject(token, projectId, request)) {
                    _updateProject.value = it
                }
            } catch (e: Exception) {
                _updateProject.value = Results.Error(e.message.toString())
            }
        }
    }
}