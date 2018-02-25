package com.vaha.server.event

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.vaha.server.notification.NotificationTypes
import com.vaha.server.util.ServerEnv

class StartSessionEvent(
    questionId: String,
    answererId: String,
    answererUsername: String,
    token: String
) {

    init {
        val message = Message.builder()
            .putData("FCM_PAYLOAD_TYPE", NotificationTypes.FCM_TYPE_START_SESSION)
            .putData("FCM_PAYLOAD_QUESTION_ID", questionId)
            .putData("FCM_PAYLOAD_ANSWERER_ID", answererId)
            .putData("FCM_PAYLOAD_ANSWERER_USERNAME", answererUsername)
            .setToken(token)
            .build()

        FirebaseMessaging.getInstance().sendAsync(message, ServerEnv.isDev())
    }
}