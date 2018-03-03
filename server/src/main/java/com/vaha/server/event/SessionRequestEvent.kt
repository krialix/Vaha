package com.vaha.server.event

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.vaha.server.notification.NotificationTypes
import com.vaha.server.util.ServerEnv

class SessionRequestEvent(token: String, username: String, content: String) {

    init {
        val message = Message.builder()
            .putData("FCM_PAYLOAD_ANSWERER_USERNAME", username)
            .putData("FCM_PAYLOAD_QUESTION_TRIMMED", content)
            .putData("FCM_PAYLOAD_TYPE", NotificationTypes.FCM_TYPE_COMMENTER_AVAILABLE)
            .setToken(token)
            .build()

        if (!ServerEnv.isTest()) {
            FirebaseMessaging.getInstance().sendAsync(message, ServerEnv.isDev())
        }
    }
}