package com.example.talentara.data.model.user

data class UserModel(
    val userId: Int,
    val email: String,
    val username: String,
    val token: String,
    val fcmToken: String,
    val isLogin: Boolean = false
)