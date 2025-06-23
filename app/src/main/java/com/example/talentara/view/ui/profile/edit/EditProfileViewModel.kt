package com.example.talentara.view.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.talent.UpdateTalentResponse
import com.example.talentara.data.model.response.user.UpdateUserResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileViewModel(private val repository: Repository) : ViewModel() {

    private val _updateUser = MediatorLiveData<Results<UpdateUserResponse>>()
    val updateUser: LiveData<Results<UpdateUserResponse>> = _updateUser

    suspend fun updateUser(
        username: String,
        email: String,
        github: String,
        linkedin: String,
        userImage: String,
        fcmToken: String,
    ) {
        viewModelScope.launch {
            _updateUser.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId
                _updateUser.addSource(
                    repository.updateUser(
                        token,
                        userId,
                        username,
                        email,
                        github,
                        linkedin,
                        userImage,
                        fcmToken
                    )
                ) { result ->
                    _updateUser.value = result
                }
            } catch (e: Exception) {
                _updateUser.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _updateTalent = MediatorLiveData<Results<UpdateTalentResponse>>()
    val updateTalent: LiveData<Results<UpdateTalentResponse>> = _updateTalent

    suspend fun updateTalent(
        request: ApiService.UpdateTalentRequest
    ) {
        viewModelScope.launch {
            _updateTalent.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId
                _updateTalent.addSource(
                    repository.updateTalent(
                        token,
                        userId,
                        request
                    )
                ) { result ->
                    _updateTalent.value = result
                }
            } catch (e: Exception) {
                _updateTalent.value = Results.Error(e.message.toString())
            }
        }
    }
}