package com.example.talentara.view.ui.portfolio.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.categories.GetAllCategoriesResponse
import com.example.talentara.data.model.response.portfolio.NewPortfolioResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewPortfolioViewModel(private val repository: Repository) : ViewModel() {

    private val _addPortfolio = MediatorLiveData<Results<NewPortfolioResponse>>()
    val addPortfolio: LiveData<Results<NewPortfolioResponse>> = _addPortfolio

    fun addPortfolio(
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

    private val _getAllCategories = MediatorLiveData<Results<GetAllCategoriesResponse>>()
    val getAllCategories: LiveData<Results<GetAllCategoriesResponse>> = _getAllCategories

    fun getAllCategories() {
        viewModelScope.launch {
            _getAllCategories.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _getAllCategories.addSource(repository.getAllCategories(token)) { result ->
                    _getAllCategories.value = result
                }
            } catch (e: Exception) {
                _getAllCategories.value = Results.Error(e.message.toString())
            }
        }
    }

}