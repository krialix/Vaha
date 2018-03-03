package com.vaha.server.question.usecase

import com.google.api.server.spi.response.BadRequestException
import com.googlecode.objectify.Key
import com.googlecode.objectify.Ref
import com.vaha.server.base.UseCase
import com.vaha.server.event.StartSessionEvent
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.Question.Status
import com.vaha.server.user.entity.Account
import com.vaha.server.util.ServerEnv

class StartSessionUseCase(
    private val questionId: String,
    private val requesterId: String
) : UseCase<Unit> {

    override fun run() {
        val questionKey = Key.create<Question>(questionId)
        val questionOwnerUserKey = questionKey.getParent<Account>()
        val requesterUserKey = Key.create<Account>(requesterId)

        if (requesterUserKey.equivalent(questionOwnerUserKey)) {
            throw BadRequestException("You can not start session for your question")
        }

        val fetched =
            ofy().load().keys(questionKey, questionOwnerUserKey, requesterUserKey) as Map<*, *>

        val question = fetched[questionKey] as Question
        val questionOwner = fetched[questionOwnerUserKey] as Account
        val requester = fetched[requesterUserKey] as Account

        if (!question.pendingUserRequests.any { it.userKey.equivalent(requesterUserKey) }) {
            throw BadRequestException("You should send session request first")
        }

        question.answerer?.let {
            if (it.equivalent(requesterUserKey)) {
                throw BadRequestException("You can not answer to same question")
            }
        }

        question.apply {
            status = Status.IN_PROGRESS
            answerer = Ref.create(requester)
            pendingUserRequests.removeIf { it.userKey.equivalent(requesterUserKey) }
        }

        requester.apply {
            answerCount++
            pendingQuestionKeys.removeIf { it.equivalent(questionKey) }
            inProgressQuestionKeys.add(questionKey)
        }

        questionOwner.inProgressQuestionKeys.add(questionKey)

        ofy().transact {
            val result = ofy().save().entities(question, questionOwner, requester)
            if (ServerEnv.isTest()) {
                result.now()
            }

            StartSessionEvent(questionId, requesterId, requester.username, requester.fcmToken)
        }
    }
}