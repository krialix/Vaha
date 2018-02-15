package com.vaha.server.question.usecase

import com.google.api.server.spi.response.BadRequestException
import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.notification.NotificationService
import com.vaha.server.notification.NotificationTypes
import com.vaha.server.notification.PushMessage
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.Question.QuestionStatus
import com.vaha.server.user.entity.Account
import com.vaha.server.util.ServerEnv

class EndSessionUseCase(
    private val questionId: String,
    private val rating: Float,
    private val reasked: Boolean,
    private val notificationService: NotificationService
) : UseCase<Unit> {

  override fun run() {
    val questionKey = Key.create<Question>(questionId)
    val questionOwnerKey = questionKey.getParent<Account>()

    val fetched = ofy().load().keys(questionKey, questionOwnerKey) as Map<*, *>

    var question = fetched[questionKey] as Question
    var questionOwner = fetched[questionOwnerKey] as Account

    var answerer = question.answerer!!.get()

    if (question.questionStatus != QuestionStatus.IN_PROGRESS) {
      throw BadRequestException("Session is not currently active")
    }

    question = question.copy(pendingAnswerers = emptySet(), answerer = null)

    questionOwner = questionOwner.copy(
        activeQuestionKeys = questionOwner.activeQuestionKeys.minus(questionKey))

    answerer = answerer.copy(
        userRating = Account.Rating.calculateNewRating(answerer.userRating, rating),
        activeQuestionKeys = answerer.activeQuestionKeys.minus(questionKey))

    var sendSessionEndedNotification = true

    question = if (reasked) {
      if (question.reAsk == 0) {
        sendSessionEndedNotification(questionOwner, answerer)
        sendSessionEndedNotification = false
        throw BadRequestException("re ask limit is over")
      }

      question.copy(questionStatus = QuestionStatus.AVAILABLE, reAsk = question.reAsk.dec())
    } else {
      question.copy(questionStatus = QuestionStatus.COMPLETED)
    }

    if (ServerEnv.isTest()) {
      ofy().save().entities(question, answerer, questionOwner).now()
    } else {
      ofy().save().entities(question, answerer, questionOwner)
    }

    if (reasked) {
      sendReaskedQuestionNotification(question, questionOwner)
    }

    if (sendSessionEndedNotification) {
      sendSessionEndedNotification(questionOwner, answerer)
    }
  }

  private fun sendReaskedQuestionNotification(question: Question, questionOwner: Account) {
    val topicName = question.category.get().topicName

    val data = mapOf(
        "FCM_PAYLOAD_USER_ID" to question.ownerWebsafeId,
        "FCM_PAYLOAD_TYPE" to NotificationTypes.FCM_TYPE_REASK,
        "FCM_PAYLOAD_CONTENT" to question.trimmedNotificationContent,
        "FCM_PAYLOAD_USERNAME" to questionOwner.username)

    notificationService.send(PushMessage("/topics/$topicName", data = data))
  }

  private fun sendSessionEndedNotification(questionOwner: Account, answerer: Account) {
    val data = mapOf(
        "FCM_PAYLOAD_TYPE" to NotificationTypes.FCM_TYPE_END_SESSION,
        "FCM_PAYLOAD_QUESTION_OWNER_USERNAME" to questionOwner.username)

    notificationService.send(PushMessage(to = answerer.fcmToken, data = data))
  }
}