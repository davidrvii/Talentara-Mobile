package com.example.talentara.data.model.user

data class UserModel(
    val email: String,
    val username: String,
    val token: String,
    val isLogin: Boolean = false
)