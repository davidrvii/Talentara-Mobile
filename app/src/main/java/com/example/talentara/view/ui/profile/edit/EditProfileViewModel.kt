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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileViewModel(private val repository: Repository) : ViewModel() {

    private val _updateUser = MediatorLiveData<Results<UpdateUserResponse>>()
    val updateUser: LiveData<Results<UpdateUserResponse>> = _updateUser

    fun updateUser(
        username: String?,
        email: String?,
        github: String?,
        linkedin: String?,
        userImage: MultipartBody.Part?,
        //fcmToken: String,
    ) {

        if (listOf(username, email, github, linkedin, userImage).all { it == null }) return

        val map = mutableMapOf<String, RequestBody>()
        username?.takeIf { it.isNotBlank() }?.let {
            map["user_name"] = it.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        email?.takeIf { it.isNotBlank() }?.let {
            map["user_email"] = it.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        github?.takeIf { it.isNotBlank() }?.let {
            map["github"] = it.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        linkedin?.takeIf { it.isNotBlank() }?.let {
            map["linkedin"] = it.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        viewModelScope.launch {
            _updateUser.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId
                _updateUser.addSource(
                    repository.updateUser(
                        token,
                        userId,
                        map,
                        userImage,
                        //fcmToken
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

    fun updateTalent(
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