package com.example.talentara.view.ui.portfolio.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.portfolio.NewPortfolioResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.launch

class NewPortfolioViewModel(private val repository: Repository) : ViewModel() {

    private val _addPortfolio = MediatorLiveData<Results<NewPortfolioResponse>>()
    val addPortfolio: LiveData<Results<NewPortfolioResponse>> = _addPortfolio

    suspend fun addPortfolio(
        request: ApiService.AddPortfolioRequest,
    ) {
        viewModelScope.launch {
            _addPortfolio.value = Results.Loading
            try {
                repository.getSession().collect { user ->
                    user.token.let { token ->
                        _addPortfolio.addSource(repository.addPortfolio(token, request)) { result ->
                            _addPortfolio.value = result
                        }
                    }
                }
            } catch (e: Exception) {
                _addPortfolio.value = Results.Error(e.message.toString())
            }
        }
    }

}