package com.example.talentara.view.ui.portfolio.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.portfolio.PortfolioDeleteResponse
import com.example.talentara.data.model.response.portfolio.PortfolioDetailResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PortfolioDetailViewModel(private val repository: Repository) : ViewModel() {

    private val _portfolioDetail = MediatorLiveData<Results<PortfolioDetailResponse>>()
    val portfolioDetail: LiveData<Results<PortfolioDetailResponse>> = _portfolioDetail

    fun getPortfolioDetail(
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

    private val _portfolioDelete = MediatorLiveData<Results<PortfolioDeleteResponse>>()
    val portfolioDelete: LiveData<Results<PortfolioDeleteResponse>> = _portfolioDelete

    fun deletePortfolio(
        portfolioId: Int,
    ) {
        viewModelScope.launch {
            _portfolioDelete.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _portfolioDelete.addSource(
                    repository.deletePortfolio(
                        token,
                        portfolioId
                    )
                ) { result ->
                    _portfolioDelete.value = result
                }

            } catch (e: Exception) {
                _portfolioDelete.value = Results.Error(e.message.toString())
            }
        }
    }
}