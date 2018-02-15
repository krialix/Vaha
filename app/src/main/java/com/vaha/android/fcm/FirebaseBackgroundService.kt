package com.vaha.android.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.vaha.android.R
import com.vaha.android.VahaApplication
import com.vaha.android.data.entity.chat.LastUserMessage
import com.vaha.android.data.repository.SessionRepository
import com.vaha.android.feature.base.MainActivity
import com.vaha.android.util.addTo
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class FirebaseBackgroundService : Service() {

    @Inject
    lateinit var sessionRepository: SessionRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val disposable = CompositeDisposable()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate()")
        VahaApplication.appComponent.inject(this)

        val userId = sharedPreferences.getString("userId", null)

        sessionRepository.observeUserMessages(userId)
            .doOnNext { Timber.d("Message: %s", it) }
            .filter { it.senderId != userId }
            .filter { it != LastUserMessage.EMPTY_LAST_MESSAGE }
            .skip(1)
            .subscribe(this::showNewMessageNotification, Timber::e)
            .addTo(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun showNewMessageNotification(message: LastUserMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("FCM_PAYLOAD_TYPE", "3")
        intent.putExtra("FCM_PAYLOAD_QUESTION_ID", message.questionId)
        intent.putExtra("FCM_PAYLOAD_ANSWERER_ID", message.senderId)
        intent.putExtra("FCM_PAYLOAD_ANSWERER_USERNAME", message.senderUsername)

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, "session")
            .setSmallIcon(R.drawable.ic_stat_vaha_icon)
            .setColor(ContextCompat.getColor(this, R.color.accent))
            .setContentTitle(getString(R.string.notification_new_message, message.senderUsername))
            .setContentText(message.lastMessage)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(ContextCompat.getColor(this, R.color.primary))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(0, notification)
    }

    private val notificationManager: NotificationManager by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}