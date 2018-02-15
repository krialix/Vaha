package com.vaha.android.fcm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vaha.android.R
import com.vaha.android.VahaApplication
import com.vaha.android.VahaService
import com.vaha.android.feature.base.MainActivity
import timber.log.Timber
import javax.inject.Inject

class VahaFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val notificationManager by lazy(LazyThreadSafetyMode.NONE) {
        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private lateinit var savedUserId: String

    override fun onCreate() {
        VahaApplication.appComponent.inject(this)
        super.onCreate()
        savedUserId = sharedPreferences.getString("userId", "")
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("onMessage(): %s", remoteMessage.data)
        buildNotification(remoteMessage)
    }

    private fun buildNotification(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val type = data["FCM_PAYLOAD_TYPE"]

        when (type) {
            "0" -> showNewQuestionNotification(data)
            "1" -> showAnswererAvailableNotification(data)
            "2" -> showSessionReadyNotification(data)
            "3" -> showSessionEndedNotification(data)
            "4" -> showReAskNotification(data)
            "5" -> showReaskedNewQuestionNotification(data)
        }
    }

    private fun showNewQuestionNotification(data: Map<String, String>) {
        val userId = data["FCM_PAYLOAD_USER_ID"]
        val username = data["FCM_PAYLOAD_USERNAME"]
        val contentText = data["FCM_PAYLOAD_CONTENT"]

        if (savedUserId != userId) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("FCM_PAYLOAD_TYPE", data["FCM_PAYLOAD_TYPE"]) // 0

            val pendingIntent = PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                0
            )

            val notification = NotificationCompat.Builder(this, "session")
                .setSmallIcon(R.drawable.ic_stat_vaha_icon)
                .setContentTitle(getString(R.string.notification_asked_question, username))
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(this, R.color.accent))
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(0, notification)
        }
    }

    private fun showAnswererAvailableNotification(data: Map<String, String>) {
        val username = data["FCM_PAYLOAD_ANSWERER_USERNAME"]
        val trimmedQuestionContent = data["FCM_PAYLOAD_QUESTION_TRIMMED"]

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("FCM_PAYLOAD_TYPE", data["FCM_PAYLOAD_TYPE"]) // 1

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "session")
            .setSmallIcon(R.drawable.ic_stat_vaha_icon)
            .setContentTitle(getString(R.string.notification_answer_question, username))
            .setContentText(trimmedQuestionContent)
            .setStyle(NotificationCompat.InboxStyle())
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.accent))
            .setChannelId("session")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun showSessionReadyNotification(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("FCM_PAYLOAD_TYPE", data["FCM_PAYLOAD_TYPE"])
        intent.putExtra("FCM_PAYLOAD_QUESTION_ID", data["FCM_PAYLOAD_QUESTION_ID"])
        intent.putExtra("FCM_PAYLOAD_ANSWERER_ID", data["FCM_PAYLOAD_ANSWERER_ID"])
        intent.putExtra("FCM_PAYLOAD_ANSWERER_USERNAME", data["FCM_PAYLOAD_ANSWERER_USERNAME"])

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "session")
            .setSmallIcon(R.drawable.ic_stat_vaha_icon)
            .setContentTitle("Vaha")
            .setContentText(getString(R.string.notification_enter_session))
            .setColor(ContextCompat.getColor(this, R.color.accent))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(2, notification)
    }

    private fun showSessionEndedNotification(data: Map<String, String>) {
        val username = data["FCM_PAYLOAD_QUESTION_OWNER_USERNAME"]

        val notification = NotificationCompat.Builder(this, "session")
            .setSmallIcon(R.drawable.ic_stat_vaha_icon)
            .setContentTitle(getString(R.string.notification_end_session, username))
            .setColor(ContextCompat.getColor(this, R.color.accent))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()

        notificationManager.notify(3, notification)
    }

    private fun showReAskNotification(data: Map<String, String>) {
        val notification = NotificationCompat.Builder(this, "session")
            .setSmallIcon(R.drawable.ic_stat_vaha_icon)
            .setContentTitle("Vaha")
            .setContentText(getString(R.string.notification_reask))
            .setColor(ContextCompat.getColor(this, R.color.accent))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()

        notificationManager.notify(4, notification)
    }

    private fun showReaskedNewQuestionNotification(data: Map<String, String>) {
        val userId = data["FCM_PAYLOAD_USER_ID"]
        val username = data["FCM_PAYLOAD_USERNAME"]
        val contentText = data["FCM_PAYLOAD_CONTENT"]

        if (savedUserId != userId) {
            val intent = Intent(this, VahaService::class.java)
            intent.putExtra("FCM_PAYLOAD_TYPE", data["FCM_PAYLOAD_TYPE"])
            intent.putExtra("FCM_PAYLOAD_QUESTION_ID", data["FCM_PAYLOAD_QUESTION_ID"])

            val pendingIntent = PendingIntent.getService(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                0
            )

            val notification = NotificationCompat.Builder(this, "session")
                .setSmallIcon(R.drawable.ic_stat_vaha_icon)
                .setContentTitle(getString(R.string.notification_reasked_question, username))
                .setContentText(contentText)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(this, R.color.accent))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(5, notification)
        }
    }
}
