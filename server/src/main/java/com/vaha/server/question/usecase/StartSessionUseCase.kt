package com.vaha.server.question.usecase

import com.google.api.server.spi.response.BadRequestException
import com.googlecode.objectify.Key
import com.googlecode.objectify.Ref
import com.vaha.server.base.UseCase
import com.vaha.server.event.StartSessionEvent
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.Question.QuestionStatus
import com.vaha.server.question.entity.UserSessionRel
import com.vaha.server.user.entity.Account
import com.vaha.server.util.ServerEnv

class StartSessionUseCase(
    private val questionId: String,
    private val userId: String,
    private val answererId: String
) : UseCase<Unit> {

    override fun run() {
        val requesterUserKey = Key.create<Account>(userId)
        val questionKey = Key.create<Question>(questionId)
        val questionOwnerUserKey = questionKey.getParent<Account>()
        val answererUserKey = Key.create<Account>(answererId)

        if (!requesterUserKey.equivalent(questionOwnerUserKey)) {
            throw BadRequestException("Only question owner can start a session")
        }

        val fetched =
            ofy().load().keys(questionKey, answererUserKey) as Map<*, *>

        val question = fetched[questionKey] as Question
        val answerer = fetched[answererUserKey] as Account

        question.questionStatus = QuestionStatus.IN_PROGRESS
        question.answerer = Ref.create(answerer)

        answerer.answerCount.inc()

        val userSessionRels = ofy().load().type(UserSessionRel::class.java)
            .filter("questionKey", questionKey)
            .list()
            .asSequence()
            .map {
                if (it.userKey.equivalent(requesterUserKey)) {
                    return@map it.copy(sessionStatus = UserSessionRel.SessionStatus.IN_PROGRESS)
                }

                return@map it.copy(sessionStatus = UserSessionRel.SessionStatus.DISCARDED)
            }
            .toList()

        val pendingSaveList = mutableListOf<Any>()
        pendingSaveList.add(question)
        pendingSaveList.add(answerer)
        pendingSaveList.addAll(userSessionRels)

        ofy().transact {
            if (ServerEnv.isTest()) {
                ofy().save().entities(pendingSaveList).now()
            } else {
                ofy().save().entities(pendingSaveList)
            }

            StartSessionEvent(questionId, answererId, answerer.username, answerer.fcmToken)
        }
    }
}