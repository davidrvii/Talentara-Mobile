package com.example.talentara.view.ui.project.offer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.project.ProjectOfferResponse
import com.example.talentara.data.model.response.talent.UpdateTalentResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProjectOfferViewModel(private val repository: Repository) : ViewModel() {

    private val _projectOffer = MediatorLiveData<Results<ProjectOfferResponse>>()
    val projectOffer: LiveData<Results<ProjectOfferResponse>> = _projectOffer

    suspend fun getProjectOffer(
        projectId: Int,
        roleName: String,
        accept: Boolean,
    ) {
        viewModelScope.launch {
            _projectOffer.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val talentId = repository.getSession().first().userId

                _projectOffer.addSource(repository.projectOffer(token, projectId, talentId, roleName, accept)) { result ->
                    _projectOffer.value = result
                }
            } catch (e: Exception) {
                _projectOffer.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTalentIsOnProject = MediatorLiveData<Results<UpdateTalentResponse>>()
    val updateTalentIsOnProject: LiveData<Results<UpdateTalentResponse>> = _updateTalentIsOnProject

    suspend fun updateTalentIsOnProject(
        isOnProject: Boolean,
    ) {
        viewModelScope.launch {
            _updateTalentIsOnProject.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val talentId = repository.getSession().first().userId

                _updateTalentIsOnProject.addSource(repository.updateTalentIsOnProject(token, talentId, isOnProject)) { result ->
                    _updateTalentIsOnProject.value = result
                }
            } catch (e: Exception) {
                _updateTalentIsOnProject.value = Results.Error(e.message.toString())
            }
        }
    }
}