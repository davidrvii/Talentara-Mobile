package com.example.talentara.view.ui.talent.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.portfolio.PortfolioTalentResponse
import com.example.talentara.data.model.response.talent.TalentDetailResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TalentDetailViewModel(private val repository: Repository) : ViewModel() {

    private val _getTalentDetail = MediatorLiveData<Results<TalentDetailResponse>>()
    val getTalentDetail: LiveData<Results<TalentDetailResponse>> = _getTalentDetail

    fun getTalentDetail(
        talentId: Int,
    ) {
        viewModelScope.launch {
            _getTalentDetail.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _getTalentDetail.addSource(repository.getTalentDetail(token, talentId)) {
                    _getTalentDetail.value = it
                }
            } catch (e: Exception) {
                _getTalentDetail.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _getTalentPortfolio = MediatorLiveData<Results<PortfolioTalentResponse>>()
    val getTalentPortfolio: LiveData<Results<PortfolioTalentResponse>> = _getTalentPortfolio

    fun getTalentPortfolio(
        talentId: Int,
    ) {
        viewModelScope.launch {
            _getTalentPortfolio.value = Results.Loading
            try {
                val token = repository.getSession().first().token

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
}