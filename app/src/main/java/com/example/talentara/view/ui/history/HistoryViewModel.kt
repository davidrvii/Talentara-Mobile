package com.example.talentara.view.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.project.ProjectHistoryResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: Repository) : ViewModel() {

    private val _getProjectHistory = MediatorLiveData<Results<ProjectHistoryResponse>>()
    val getProjectHistory: LiveData<Results<ProjectHistoryResponse>> = _getProjectHistory

    suspend fun getProjectHistory() {
        viewModelScope.launch {
            _getProjectHistory.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId

                _getProjectHistory.addSource(
                    repository.getProjectHistory(
                        token,
                        userId
                    )
                ) { result ->
                    _getProjectHistory.value = result
                }

            } catch (e: Exception) {
                _getProjectHistory.value = Results.Error(e.message.toString())
            }
        }
    }
}