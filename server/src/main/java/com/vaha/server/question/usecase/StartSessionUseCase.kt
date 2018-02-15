package com.vaha.server.question.usecase

import com.google.api.server.spi.response.BadRequestException
import com.googlecode.objectify.Key
import com.googlecode.objectify.Ref
import com.vaha.server.base.UseCase
import com.vaha.server.notification.NotificationService
import com.vaha.server.notification.NotificationTypes
import com.vaha.server.notification.PushMessage
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.Question.QuestionStatus
import com.vaha.server.user.entity.Account
import com.vaha.server.util.ServerEnv

class StartSessionUseCase(
    private val questionId: String,
    private val userId: String,
    private val answererId: String,
    private val notificationService: NotificationService
) : UseCase<Unit> {

  override fun run() {
    val requesterUserKey = Key.create<Account>(userId)
    val questionKey = Key.create<Question>(questionId)
    val questionOwnerUserKey = questionKey.getParent<Account>()
    val answererUserKey = Key.create<Account>(answererId)

    if (!requesterUserKey.equivalent(questionOwnerUserKey)) {
      throw BadRequestException("Only question owner can start a session")
    }

    val fetched = ofy().load().keys(questionKey, questionOwnerUserKey, answererUserKey) as Map<*, *>

    var question = fetched[questionKey] as Question
    var questionOwner = fetched[questionOwnerUserKey] as Account
    var answerer = fetched[answererUserKey] as Account

    question = question.copy(questionStatus = QuestionStatus.IN_PROGRESS,
        answerer = Ref.create(answerer), pendingAnswerers = emptySet())

    questionOwner = questionOwner.copy(
        questionKeys = questionOwner.questionKeys.plus(questionKey),
        activeQuestionKeys = questionOwner.activeQuestionKeys.plus(questionKey))

    answerer = answerer.copy(
        questionKeys = answerer.questionKeys.plus(questionKey),
        activeQuestionKeys = answerer.activeQuestionKeys.plus(questionKey),
        answerCount = answerer.answerCount.inc())

    ofy().transact {
      if (ServerEnv.isTest()) {
        ofy().save().entities(question, questionOwner, answerer).now()
      } else {
        ofy().save().entities(question, questionOwner, answerer)
      }

      val data = mapOf(
          "FCM_PAYLOAD_TYPE" to NotificationTypes.FCM_TYPE_START_SESSION,
          "FCM_PAYLOAD_QUESTION_ID" to question.websafeId,
          "FCM_PAYLOAD_ANSWERER_ID" to answerer.websafeId,
          "FCM_PAYLOAD_ANSWERER_USERNAME" to answerer.username)

      val pushMessage = PushMessage(to = answerer.fcmToken, data = data)

      // send fcm to answerer
      notificationService.send(pushMessage)
    }
  }
}