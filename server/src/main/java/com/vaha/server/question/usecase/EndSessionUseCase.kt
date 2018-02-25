package com.vaha.server.question.usecase

import com.google.api.server.spi.response.BadRequestException
import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.event.ReaskedQuestionEvent
import com.vaha.server.event.SessionEndedEvent
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.Question.QuestionStatus
import com.vaha.server.question.entity.UserSessionRel
import com.vaha.server.user.entity.Account
import com.vaha.server.util.ServerEnv

class EndSessionUseCase(
    private val questionId: String,
    private val rating: Float,
    private val reasked: Boolean
) : UseCase<Unit> {

    override fun run() {
        val questionKey = Key.create<Question>(questionId)
        val questionOwnerKey = questionKey.getParent<Account>()

        val fetched = ofy().load().keys(questionKey, questionOwnerKey) as Map<*, *>

        var question = fetched[questionKey] as Question
        val questionOwner = fetched[questionOwnerKey] as Account

        var answerer = question.answerer!!.get()

        if (question.questionStatus != QuestionStatus.IN_PROGRESS) {
            throw BadRequestException("Session is not currently active")
        }

        question = question.copy(answerer = null)

        val userSessionRel =
            ofy().load().type(UserSessionRel::class.java)
                .filter("questionKey", questionKey)
                .filter("sessionStatus", UserSessionRel.SessionStatus.IN_PROGRESS)
                .first()
                .now()

        userSessionRel.sessionStatus = UserSessionRel.SessionStatus.COMPLETED

        answerer.userRating = Account.Rating.calculateNewRating(answerer.userRating, rating)

        var sendSessionEndedNotification = true

        question = if (reasked) {
            if (question.reAsk == 0) {
                SessionEndedEvent(questionOwner.username, answerer.fcmToken)
                sendSessionEndedNotification = false
                throw BadRequestException("re ask limit is over")
            }

            question.copy(questionStatus = QuestionStatus.AVAILABLE, reAsk = question.reAsk.dec())
        } else {
            question.copy(questionStatus = QuestionStatus.COMPLETED)
        }

        if (ServerEnv.isTest()) {
            ofy().save().entities(question, answerer, userSessionRel).now()
        } else {
            ofy().save().entities(question, answerer, userSessionRel)
        }

        if (reasked) {
            ReaskedQuestionEvent(
                question.ownerWebsafeId,
                question.trimmedNotificationContent,
                questionOwner.username,
                question.category.get().topicName
            )
        }

        if (sendSessionEndedNotification) {
            SessionEndedEvent(questionOwner.username, answerer.fcmToken)
        }
    }
}