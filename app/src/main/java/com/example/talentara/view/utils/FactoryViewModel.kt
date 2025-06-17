package com.example.talentara.view.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.talentara.data.repository.Repository
import com.example.talentara.injection.Data
import com.example.talentara.view.ui.authentication.AuthenticationViewModel
import com.example.talentara.view.ui.history.HistoryViewModel
import com.example.talentara.view.ui.home.HomeViewModel
import com.example.talentara.view.ui.main.MainViewModel
import com.example.talentara.view.ui.notifications.NotificationsViewModel
import com.example.talentara.view.ui.portfolio.add.NewPortfolioViewModel
import com.example.talentara.view.ui.portfolio.detail.PortfolioDetailViewModel
import com.example.talentara.view.ui.profile.ProfileViewModel
import com.example.talentara.view.ui.profile.edit.EditProfileViewModel
import com.example.talentara.view.ui.project.add.NewProjectViewModel
import com.example.talentara.view.ui.project.detail.ProjectDetailViewModel
import com.example.talentara.view.ui.project.finalize.ProjectFinalizeViewModel
import com.example.talentara.view.ui.project.offer.ProjectOfferViewModel
import com.example.talentara.view.ui.talent.apply.TalentApplyViewModel
import com.example.talentara.view.ui.talent.detail.TalentDetailViewModel
import com.example.talentara.view.ui.timeline.TimelineViewModel

class FactoryViewModel(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {

    private val mainViewModelInstance: MainViewModel by lazy {
        MainViewModel(repository)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(AuthenticationViewModel::class.java) -> {
                AuthenticationViewModel(repository) as T
            }

            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                mainViewModelInstance as T
            }

            modelClass.isAssignableFrom(NotificationsViewModel::class.java) -> {
                NotificationsViewModel(repository) as T
            }

            modelClass.isAssignableFrom(NewPortfolioViewModel::class.java) -> {
                NewPortfolioViewModel(repository) as T
            }

            modelClass.isAssignableFrom(PortfolioDetailViewModel::class.java) -> {
                PortfolioDetailViewModel(repository) as T
            }

            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }

            modelClass.isAssignableFrom(NewProjectViewModel::class.java) -> {
                NewProjectViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProjectDetailViewModel::class.java) -> {
                ProjectDetailViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProjectFinalizeViewModel::class.java) -> {
                ProjectFinalizeViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProjectOfferViewModel::class.java) -> {
                ProjectOfferViewModel(repository) as T
            }

            modelClass.isAssignableFrom(TalentApplyViewModel::class.java) -> {
                TalentApplyViewModel(repository) as T
            }

            modelClass.isAssignableFrom(TalentDetailViewModel::class.java) -> {
                TalentDetailViewModel(repository) as T
            }

            modelClass.isAssignableFrom(TimelineViewModel::class.java) -> {
                TimelineViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)

        }
    }

    companion object {
        @Volatile
        private var instance: FactoryViewModel? = null

        fun getInstance(context: Context): FactoryViewModel =
            instance ?: synchronized(FactoryViewModel::class.java) {
                instance ?: FactoryViewModel(Data.provideCACRepository(context))
            }.also {
                instance = it
            }
    }

}