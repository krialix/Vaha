package com.vaha.server.event

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.vaha.server.notification.NotificationTypes
import com.vaha.server.util.ServerEnv

class SessionEndedEvent(username: String, token: String) {

    init {
        val message = Message.builder()
            .putData("FCM_PAYLOAD_TYPE", NotificationTypes.FCM_TYPE_END_SESSION)
            .putData("FCM_PAYLOAD_QUESTION_OWNER_USERNAME", username)
            .setToken(token)
            .build()

        if (!ServerEnv.isTest()) {
            FirebaseMessaging.getInstance().sendAsync(message, ServerEnv.isDev())
        }
    }
}