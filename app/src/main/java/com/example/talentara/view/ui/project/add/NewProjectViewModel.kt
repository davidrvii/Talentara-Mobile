package com.example.talentara.view.ui.project.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.project.NewProjectResponse
import com.example.talentara.data.model.response.user.UpdateUserResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewProjectViewModel(private val repository: Repository) : ViewModel() {

    private val _addProject = MediatorLiveData<Results<NewProjectResponse>>()
    val addProject: LiveData<Results<NewProjectResponse>> = _addProject

    fun addProject(
        clientName: String,
        projectName: String,
        projectDesc: String,
        startDate: String,
        endDate: String,
    ) {
        viewModelScope.launch {
            _addProject.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val statusId = 1

                _addProject.addSource(
                    repository.addProject(
                        token,
                        statusId,
                        clientName,
                        projectName,
                        projectDesc,
                        startDate,
                        endDate
                    )
                ) { result ->
                    _addProject.value = result
                }
            } catch (e: Exception) {
                _addProject.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateUserIsOnProject = MediatorLiveData<Results<UpdateUserResponse>>()
    val updateUserIsOnProject: LiveData<Results<UpdateUserResponse>> = _updateUserIsOnProject

    fun updateUserIsOnProject(
        isOnProject: Int,
    ) {
        viewModelScope.launch {
            _updateUserIsOnProject.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId

                _updateUserIsOnProject.addSource(repository.updateUserIsOnProject(token, userId, isOnProject)) { result ->
                    _updateUserIsOnProject.value = result
                }
            } catch (e: Exception) {
                _updateUserIsOnProject.value = Results.Error(e.message.toString())
            }
        }
    }
}