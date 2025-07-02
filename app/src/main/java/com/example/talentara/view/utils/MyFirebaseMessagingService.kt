package com.example.talentara.view.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.talentara.R
import com.example.talentara.data.local.preference.UserPreference
import com.example.talentara.data.local.preference.dataStore
import com.example.talentara.data.remote.ApiConfig
import com.example.talentara.view.ui.main.MainActivity
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import com.example.talentara.view.ui.project.offer.ProjectOfferActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pref = UserPreference.getInstance(applicationContext.dataStore)
                val session = pref.getSession().first()
                val userId = session.userId
                val userToken = session.token
                val fcmToken = token
                if (userId > 0 && token.isNotBlank()) {
                    val response = ApiConfig.getApiService().updateFcmToken("Bearer $userToken", fcmToken)
                    applicationContext.dataStore.edit { prefs ->
                        prefs[stringPreferencesKey("fcm_token")] = fcmToken
                    }
                    Log.d(TAG, "FCM token updated: $response")
                } else {
                    applicationContext.dataStore.edit { prefs ->
                        prefs[stringPreferencesKey("fcm_token")] = fcmToken
                    }
                    Log.w(TAG, "User not logged in – skip sending FCM token")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending FCM token to server", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val title = remoteMessage.notification?.title ?: data["notification_title"]
        val body = remoteMessage.notification?.body ?: data["notification_desc"]
        val clickAction = data["click_action"]
        val refId = data["reference_id"]?.toIntOrNull()

        val intent = when (clickAction) {
            "OPEN_PROJECT_OFFER" -> Intent(this, ProjectOfferActivity::class.java).apply {
                putExtra("project_id", refId)
            }

            "OPEN_PROJECT_DETAIL" -> Intent(this, ProjectDetailActivity::class.java).apply {
                putExtra("project_id", refId)
            }

            else -> Intent(this, MainActivity::class.java)
        }.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            refId ?: 0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_portfolio_inside)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        nm.notify(refId ?: System.currentTimeMillis().toInt(), builder.build())
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        private const val NOTIFICATION_CHANNEL_ID = "Firebase Channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Firebase Notification"
    }
}