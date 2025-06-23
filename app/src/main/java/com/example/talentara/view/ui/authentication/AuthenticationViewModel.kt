package com.example.talentara.view.ui.authentication

import androidx.lifecycle.ViewModel
import com.example.talentara.data.model.response.user.RegisterResponse
import com.example.talentara.data.repository.Repository

class AuthenticationViewModel(private val repository: Repository) : ViewModel() {

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): RegisterResponse {
        return repository.register(username, email, password)
    }

    suspend fun login(
        email: String,
        password: String
    ) {
        return repository.login(email, password)
    }
}