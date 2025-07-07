package com.example.talentara.view.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talentara.data.model.response.notification.DeleteNotificationResponse
import com.example.talentara.data.model.response.notification.NewNotificationResponse
import com.example.talentara.data.model.response.notification.NotificationHistoryResponse
import com.example.talentara.data.model.response.notification.UpdateNotificationResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationsViewModel(private val repository: Repository) : ViewModel() {

    private val _notificationHistory = MediatorLiveData<Results<NotificationHistoryResponse>>()
    val notificationHistory: LiveData<Results<NotificationHistoryResponse>> = _notificationHistory

    fun getNotificationHistory() {
        viewModelScope.launch {
            _notificationHistory.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId

                _notificationHistory.addSource(
                    repository.getNotificationHistory(
                        token,
                        userId
                    )
                ) { result ->
                    _notificationHistory.value = result
                }

            } catch (e: Exception) {
                _notificationHistory.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _addNotification = MediatorLiveData<Results<NewNotificationResponse>>()
    val addNotification: LiveData<Results<NewNotificationResponse>> = _addNotification

    fun addNotification(
        title: String,
        desc: String,
        type: String,
        clickAction: String
    ) {
        viewModelScope.launch {
            _addNotification.value = Results.Loading
            try {
                val token = repository.getSession().first().token
                val userId = repository.getSession().first().userId

                _addNotification.addSource(
                    repository.addNotification(
                        token,
                        userId,
                        title,
                        desc,
                        type,
                        clickAction
                    )
                ) { result ->
                    _addNotification.value = result
                }
            } catch (e: Exception) {
                _addNotification.value = Results.Error(e.message.toString())
            }
        }
    }

    suspend fun addNotificationTalent(
        userId: Int,
        title: String,
        desc: String,
        type: String,
        clickAction: String
    ): Results<NewNotificationResponse> {
        return try {
            val token = repository.getSession().first().token
            repository.addNotificationTalent(token, userId, title, desc, type, clickAction)
        } catch (e: Exception) {
            Results.Error(e.message ?: "Unknown error")
        }
    }

    private val _updateNotification = MediatorLiveData<Results<UpdateNotificationResponse>>()
    val updateNotification: LiveData<Results<UpdateNotificationResponse>> = _updateNotification

    fun updateNotification(
        status: String,
        notificationId: Int
    ) {
        viewModelScope.launch {
            _updateNotification.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _updateNotification.addSource(repository.updateNotification(token, notificationId, status)) { result ->
                    _updateNotification.value = result
                }
            } catch (e: Exception) {
                _updateNotification.value = Results.Error(e.message.toString())
            }
        }
    }

    private val _deleteNotification = MediatorLiveData<Results<DeleteNotificationResponse>>()
    val deleteNotification: LiveData<Results<DeleteNotificationResponse>> = _deleteNotification

    fun deleteNotification(
        notificationId: Int
    ) {
        viewModelScope.launch {
            _deleteNotification.value = Results.Loading
            try {
                val token = repository.getSession().first().token

                _deleteNotification.addSource(repository.deleteNotification(token, notificationId)) { result ->
                    _deleteNotification.value = result
                }
            } catch (e: Exception) {
                _deleteNotification.value = Results.Error(e.message.toString())
            }
        }
    }
}