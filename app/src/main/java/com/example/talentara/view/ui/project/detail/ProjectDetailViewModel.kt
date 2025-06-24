package com.example.talentara.view.ui.project.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.project.ProjectDetailResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProjectDetailViewModel(private val repository: Repository) : ViewModel() {

    private val _getProjectDetail = MediatorLiveData<Results<ProjectDetailResponse>>()
    val getProjectDetail: LiveData<Results<ProjectDetailResponse>> = _getProjectDetail

    fun getProjectDetail(
        projectId: Int,
    ) {
        viewModelScope.launch {
            _getProjectDetail.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _getProjectDetail.addSource(
                    repository.getProjectDetail(
                        token,
                        projectId
                    )
                ) { result ->
                    _getProjectDetail.value = result
                }
            } catch (e: Exception) {
                _getProjectDetail.value = Results.Error(e.message.toString())
            }
        }
    }
}