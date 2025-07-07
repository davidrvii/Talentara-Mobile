package com.example.talentara.view.ui.project.offer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.project.ProjectOfferResponse
import com.example.talentara.data.model.response.project.ProjectOrderResponse
import com.example.talentara.data.model.response.user.UpdateUserResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProjectOfferViewModel(private val repository: Repository) : ViewModel() {

    private val _projectOffer = MediatorLiveData<Results<ProjectOfferResponse>>()
    val projectOffer: LiveData<Results<ProjectOfferResponse>> = _projectOffer

    fun getProjectOffer(
        projectId: Int,
        roleName: String,
        accept: Int,
    ) {
        viewModelScope.launch {
            _projectOffer.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val talentId = repository.getSession().first().userId

                _projectOffer.addSource(
                    repository.projectOffer(
                        token,
                        projectId,
                        talentId,
                        roleName,
                        accept
                    )
                ) { result ->
                    _projectOffer.value = result
                }
            } catch (e: Exception) {
                _projectOffer.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateUserIsOnProjectDone = MediatorLiveData<Results<UpdateUserResponse>>()
    val updateUserIsOnProjectDone: LiveData<Results<UpdateUserResponse>> =
        _updateUserIsOnProjectDone

    fun updateUserIsOnProjectDone(
        talendId: Int,
        isOnProject: Int,
    ) {
        viewModelScope.launch {
            _updateUserIsOnProjectDone.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _updateUserIsOnProjectDone.addSource(
                    repository.updateUserIsOnProject(
                        token,
                        talendId,
                        isOnProject
                    )
                ) { result ->
                    _updateUserIsOnProjectDone.value = result
                }
            } catch (e: Exception) {
                _updateUserIsOnProjectDone.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTalentIsOnProjectDone = MediatorLiveData<Results<UpdateUserResponse>>()
    val updateTalentIsOnProjectDone: LiveData<Results<UpdateUserResponse>> =
        _updateTalentIsOnProjectDone

    fun updateTalentIsOnProjectDone(
        talendId: Int,
        isOnProject: Int,
    ) {
        viewModelScope.launch {
            _updateTalentIsOnProjectDone.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _updateTalentIsOnProjectDone.addSource(
                    repository.updateUserIsOnProject(
                        token,
                        talendId,
                        isOnProject
                    )
                ) { result ->
                    _updateTalentIsOnProjectDone.value = result
                }
            } catch (e: Exception) {
                _updateTalentIsOnProjectDone.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getProjectOrder = MediatorLiveData<Results<ProjectOrderResponse>>()
    val getProjectOrder: LiveData<Results<ProjectOrderResponse>> = _getProjectOrder

    fun getProjectOrder(
        projectId: Int,
    ) {
        viewModelScope.launch {
            _getProjectOrder.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _getProjectOrder.addSource(repository.getProjectOrder(token, projectId)) { result ->
                    _getProjectOrder.value = result
                }
            } catch (e: Exception) {
                _getProjectOrder.value = Results.Error(e.message.toString())
            }
        }
    }
}