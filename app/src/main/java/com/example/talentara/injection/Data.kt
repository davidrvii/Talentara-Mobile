package com.example.talentara.injection

import com.example.talentara.data.remote.ApiConfig
import android.content.Context
import com.example.talentara.data.local.preference.UserPreference
import com.example.talentara.data.local.preference.dataStore
import com.example.talentara.data.repository.Repository


object Data {
    fun provideCACRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()

        return Repository.getInstance(pref, apiService)
    }
}