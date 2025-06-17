package com.example.talentara.data.repository

import com.example.talentara.data.local.preference.UserPreference
import com.example.talentara.data.remote.ApiService

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
){


    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService)
            }.also { instance = it }
    }
}