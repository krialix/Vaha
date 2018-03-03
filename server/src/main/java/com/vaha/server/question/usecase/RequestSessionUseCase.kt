package com.vaha.server.question.usecase

import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.event.SessionRequestEvent
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.user.entity.Account
import com.vaha.server.util.ServerEnv

class RequestSessionUseCase(private val userId: String, private val questionId: String) :
    UseCase<Unit> {

    override fun run() {
        val questionOwnerKey = Key.create<Question>(questionId).getParent<Account>()
        val requesterKey = Key.create<Account>(userId)
        val questionKey = Key.create<Question>(questionId)

        val fetched =
            ofy().load().keys(questionOwnerKey, questionKey, requesterKey) as Map<*, *>
        val questionOwner = fetched[questionOwnerKey] as Account
        val requester = fetched[requesterKey] as Account
        val question = fetched[questionKey] as Question

        question.pendingUserRequests.add(
            Question.PendingUserRequest(
                userKey = requester.key,
                username = requester.username,
                rating = requester.userRating.rating
            )
        )

        requester.pendingQuestionKeys.add(questionKey)

        val result = ofy().save().entities(question, requester)
        if (ServerEnv.isTest()) {
            result.now()
        }

        SessionRequestEvent(
            questionOwner.fcmToken,
            requester.username,
            question.trimmedNotificationContent
        )
    }
}