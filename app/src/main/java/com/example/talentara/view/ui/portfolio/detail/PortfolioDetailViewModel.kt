package com.example.talentara.view.ui.portfolio.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.portfolio.PortfolioDetailResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PortfolioDetailViewModel(private val repository: Repository) : ViewModel() {

    private val _portfolioDetail = MediatorLiveData<Results<PortfolioDetailResponse>>()
    val portfolioDetail: LiveData<Results<PortfolioDetailResponse>> = _portfolioDetail

    suspend fun getPortfolioDetail(
        portfolioId: Int,
    ) {
        viewModelScope.launch {
            _portfolioDetail.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _portfolioDetail.addSource(
                    repository.getPortfolioDetail(
                        token,
                        portfolioId
                    )
                ) { result ->
                    _portfolioDetail.value = result
                }

            } catch (e: Exception) {
                _portfolioDetail.value = Results.Error(e.message.toString())
            }
        }
    }
}