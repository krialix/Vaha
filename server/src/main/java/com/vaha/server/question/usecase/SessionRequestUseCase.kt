package com.vaha.server.question.usecase

import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.event.SessionRequestEvent
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.UserSessionRel
import com.vaha.server.user.entity.Account

class SessionRequestUseCase(private val userId: String, private val questionId: String) :
    UseCase<Unit> {

    override fun run() {
        val questionOwnerKey = Key.create<Question>(questionId).getParent<Account>()
        val answererKey = Key.create<Account>(userId)
        val questionKey = Key.create<Question>(questionId)

        val fetched =
            ofy().load().keys(questionOwnerKey, questionKey, answererKey) as Map<*, *>
        val questionOwner = fetched[questionOwnerKey] as Account
        val pendingAnswerer = fetched[answererKey] as Account
        val question = fetched[questionKey] as Question

        val userSessionRel = UserSessionRel(
            userKey = answererKey,
            questionKey = questionKey,
            sessionStatus = UserSessionRel.SessionStatus.PENDING
        )

        ofy().save().entities(question, userSessionRel)

        SessionRequestEvent(
            questionOwner.fcmToken,
            pendingAnswerer.username,
            question.trimmedNotificationContent
        )
    }
}