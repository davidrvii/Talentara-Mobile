package com.example.talentara.view.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.user.LoginResponse
import com.example.talentara.data.model.response.user.RegisterResponse
import com.example.talentara.data.model.response.user.SaveFcmTokenResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.model.user.UserModel
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val repository: Repository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    private val _register = MediatorLiveData<Results<RegisterResponse>>()
    val register: LiveData<Results<RegisterResponse>> = _register

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _register.value = Results.Loading
            try {
                val response = repository.register(username, email, password)
                _register.value = Results.Success(response)
            } catch (e: Exception) {
                _register.value = Results.Error(e.message.toString())
            }
        }
    }


    private val _login = MediatorLiveData<Results<LoginResponse>>()
    val login: LiveData<Results<LoginResponse>> = _login

    suspend fun login(
        email: String,
        password: String
    ) {
        return repository.login(email, password)
    }

    private val _saveFcmTokenResponse = MediatorLiveData<Results<SaveFcmTokenResponse>>()
    val saveFcmTokenResponse: LiveData<Results<SaveFcmTokenResponse>> get() = _saveFcmTokenResponse

    fun saveFcmToken(fcmToken: String) {
        viewModelScope.launch {
            _saveFcmTokenResponse.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                _saveFcmTokenResponse.addSource(repository.saveFcmToken(token, fcmToken)) { result ->
                    _saveFcmTokenResponse.value = result
                }
            } catch (e: Exception) {
                _saveFcmTokenResponse.value = Results.Error(e.message.toString())
            }
        }
    }
}