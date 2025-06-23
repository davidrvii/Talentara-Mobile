package com.example.talentara.view.ui.talent.apply

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.talent.NewTalentResponse
import com.example.talentara.data.model.response.user.UpdateUserResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TalentApplyViewModel(private val repository: Repository) : ViewModel() {

    private val _addTalent = MediatorLiveData<Results<NewTalentResponse>>()
    val addTalent: LiveData<Results<NewTalentResponse>> = _addTalent

    suspend fun addTalent(
        request: ApiService.AddTalentRequest,
    ) {
        _addTalent.value = Results.Loading
        try {
            val token = repository.getSession().first().token
            _addTalent.addSource(repository.addTalent(token, request)) { result ->
                _addTalent.value = result
            }
        } catch (e: Exception) {
            _addTalent.value = Results.Error(e.message.toString())
        }
    }

    private val _updateUserTalentAccess = MediatorLiveData<Results<UpdateUserResponse>>()
    val updateUserTalentAccess: LiveData<Results<UpdateUserResponse>> = _updateUserTalentAccess

    suspend fun updateUserTalentAccess(
        talentAccess: Boolean,
    ) {
        viewModelScope.launch {
            _updateUserTalentAccess.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId

                _updateUserTalentAccess.addSource(repository.updateUserTalentAccess(token, userId, talentAccess)) { result ->
                    _updateUserTalentAccess.value = result
                }
            } catch (e: Exception) {
                _updateUserTalentAccess.value = Results.Error(e.message.toString())
            }
        }
    }
}