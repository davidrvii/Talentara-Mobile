package com.example.talentara.view.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.portfolio.PortfolioTalentResponse
import com.example.talentara.data.model.response.talent.TalentDetailResponse
import com.example.talentara.data.model.response.talent.UpdateTalentResponse
import com.example.talentara.data.model.response.user.UserDetailResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository) : ViewModel() {

    private val _getUserDetail = MediatorLiveData<Results<UserDetailResponse>>()
    val getUserDetail: LiveData<Results<UserDetailResponse>> = _getUserDetail

    suspend fun getUserDetail() {
        viewModelScope.launch {
            _getUserDetail.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId

                _getUserDetail.addSource(repository.getUserDetail(token, userId)) { result ->
                    _getUserDetail.value = result
                }

            } catch (e: Exception) {
                _getUserDetail.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getTalentDetail = MediatorLiveData<Results<TalentDetailResponse>>()
    val getTalentDetail: LiveData<Results<TalentDetailResponse>> = _getTalentDetail

    suspend fun getTalentDetail() {
        viewModelScope.launch {
            _getTalentDetail.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val talentId = repository.getSession().first().userId

                _getTalentDetail.addSource(repository.getTalentDetail(token, talentId)) { result ->
                    _getTalentDetail.value = result
                }
            } catch (e: Exception) {
                _getTalentDetail.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getTalentPortfolio = MediatorLiveData<Results<PortfolioTalentResponse>>()
    val getTalentPortfolio: LiveData<Results<PortfolioTalentResponse>> = _getTalentPortfolio

    suspend fun getTalentPortfolio(
    ) {
        viewModelScope.launch {
            _getTalentPortfolio.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val talentId = repository.getSession().first().userId

                _getTalentPortfolio.addSource(
                    repository.getTalentPortfolio(
                        token,
                        talentId
                    )
                ) { result ->
                    _getTalentPortfolio.value = result
                }
            } catch (e: Exception) {
                _getTalentPortfolio.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTalentAvailability = MediatorLiveData<Results<UpdateTalentResponse>>()
    val updateTalentAvailability: LiveData<Results<UpdateTalentResponse>> = _updateTalentAvailability

    suspend fun updateTalentAvailability(
        availability: Boolean
    ) {
        viewModelScope.launch {
            _updateTalentAvailability.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val talentId = repository.getSession().first().userId

                _updateTalentAvailability.addSource(
                    repository.updateTalentAvailability(
                        token,
                        talentId,
                        availability
                        )
                ) { result ->
                    _updateTalentAvailability.value = result
                }
            } catch (e: Exception) {
                _updateTalentAvailability.value = Results.Error(e.message.toString())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}